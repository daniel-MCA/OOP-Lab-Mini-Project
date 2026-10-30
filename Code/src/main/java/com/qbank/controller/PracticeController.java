package com.qbank.controller;

import com.qbank.dto.QuestionDtos.*;
import com.qbank.model.*;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.TestAttemptRepository;
import com.qbank.repository.UserRepository;
import com.qbank.service.ResponseService;
import com.qbank.service.TestCompositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final TestCompositionService testCompositionService;
    private final ResponseService responseService;

    public PracticeController(UserRepository userRepository, QuestionRepository questionRepository,
                               TestAttemptRepository testAttemptRepository,
                               TestCompositionService testCompositionService, ResponseService responseService) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.testCompositionService = testCompositionService;
        this.responseService = responseService;
    }

    /** Adaptive practice: get the single best-next question for this learner. */
    @GetMapping("/next")
    public ResponseEntity<?> next(@RequestParam Long userId, @RequestParam String subject,
                                   @RequestParam(required = false) String topic,
                                   @RequestParam(required = false) String excludeIds) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        Set<Long> exclude = new HashSet<>();
        if (excludeIds != null && !excludeIds.isBlank()) {
            for (String s : excludeIds.split(",")) exclude.add(Long.parseLong(s.trim()));
        }

        return testCompositionService.nextAdaptiveQuestion(user, subject, topic, exclude)
                .map(q -> ResponseEntity.ok(toPublic(q)))
                .orElse(ResponseEntity.noContent().build());
    }

    /** Compose a full test (mode=TEST) or a time-attack set (mode=TIME_ATTACK). */
    @PostMapping("/compose")
    public ResponseEntity<?> compose(@RequestParam Long userId, @RequestParam String subject,
                                      @RequestParam(defaultValue = "10") int count,
                                      @RequestParam(defaultValue = "TEST") String mode) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        List<Question> questions = "TIME_ATTACK".equalsIgnoreCase(mode)
                ? testCompositionService.composeTimeAttack(user, subject, count)
                : testCompositionService.composeTest(user, subject, count);

        TestAttempt attempt = new TestAttempt();
        attempt.setUser(user);
        attempt.setSubject(subject);
        attempt.setMode(mode);
        attempt.setTotalQuestions(questions.size());
        testAttemptRepository.save(attempt);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("testAttemptId", attempt.getId());
        body.put("questions", questions.stream().map(this::toPublic).collect(Collectors.toList()));
        return ResponseEntity.ok(body);
    }

    /** Submit an answer for practice, a test question, or time-attack. Triggers real-time recalibration. */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmitAnswerRequest req) {
        User user = userRepository.findById(req.userId).orElse(null);
        Question question = questionRepository.findById(req.questionId).orElse(null);
        if (user == null || question == null) return ResponseEntity.notFound().build();

        TestAttempt attempt = req.testAttemptId != null ? testAttemptRepository.findById(req.testAttemptId).orElse(null) : null;

        Response response = responseService.recordAttempt(user, question, req.selectedOption, req.timeTakenMs,
                req.mode == null ? "PRACTICE" : req.mode, attempt);

        if (attempt != null) {
            attempt.setCorrectAnswers(attempt.getCorrectAnswers() + (response.isCorrect() ? 1 : 0));
            testAttemptRepository.save(attempt);
        }

        boolean isExam = "TEST".equalsIgnoreCase(req.mode);

        SubmitAnswerResponse out = new SubmitAnswerResponse();
        out.xpEarned = user.getXp();
        out.newAbility = user.getAbility(question.getSubject());

        if (isExam) {
            // Exam mode: don't reveal correctness, the right answer, the
            // explanation, or the live difficulty per question — all of that
            // is only shown afterwards via the /report endpoint, once the
            // whole attempt is finished. Leaving these null means they are
            // omitted from the JSON entirely, not just hidden in the UI.
            out.correct = null;
            out.correctOption = null;
            out.explanation = null;
            out.newDifficulty = null;
        } else {
            out.correct = response.isCorrect();
            out.correctOption = question.getCorrectOption();
            out.explanation = question.getExplanation();
            out.newDifficulty = question.getDifficulty();
        }
        return ResponseEntity.ok(out);
    }

    /** Post-exam report: every question in the attempt, what the student picked, what was correct, why, and how long it took. */
    @GetMapping("/report/{attemptId}")
    public ResponseEntity<?> report(@PathVariable Long attemptId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId).orElse(null);
        if (attempt == null) return ResponseEntity.notFound().build();

        ReportSummary summary = new ReportSummary();
        summary.attemptId = attempt.getId();
        summary.subject = attempt.getSubject();
        summary.mode = attempt.getMode();
        summary.totalQuestions = attempt.getTotalQuestions();
        summary.correctAnswers = attempt.getCorrectAnswers();
        summary.scorePercent = attempt.getScorePercent();
        summary.startedAt = attempt.getStartedAt() == null ? null : attempt.getStartedAt().toString();
        summary.completedAt = attempt.getCompletedAt() == null ? null : attempt.getCompletedAt().toString();

        summary.items = attempt.getResponses().stream().map(r -> {
            Question q = r.getQuestion();
            ReportItem item = new ReportItem();
            item.questionId = q.getId();
            item.topic = q.getTopic();
            item.questionText = q.getQuestionText();
            item.optionA = q.getOptionA();
            item.optionB = q.getOptionB();
            item.optionC = q.getOptionC();
            item.optionD = q.getOptionD();
            item.selectedOption = r.getSelectedOption();
            item.correctOption = q.getCorrectOption();
            item.correct = r.isCorrect();
            item.explanation = q.getExplanation();
            item.timeTakenMs = r.getTimeTakenMs();
            item.difficultyAtAttempt = r.getDifficultyAtAttempt();
            item.difficultyLabelAtAttempt = Question.difficultyLabelFor(r.getDifficultyAtAttempt());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(summary);
    }

    /** Finalize a test attempt (call when the student finishes a TEST or TIME_ATTACK set). */
    @PostMapping("/{attemptId}/finish")
    public ResponseEntity<?> finish(@PathVariable Long attemptId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId).orElse(null);
        if (attempt == null) return ResponseEntity.notFound().build();

        double pct = attempt.getTotalQuestions() == 0 ? 0
                : (100.0 * attempt.getCorrectAnswers() / attempt.getTotalQuestions());
        attempt.setScorePercent(Math.round(pct * 100.0) / 100.0);
        attempt.setCompletedAt(java.time.LocalDateTime.now());
        testAttemptRepository.save(attempt);
        return ResponseEntity.ok(attempt);
    }

    private QuestionPublic toPublic(Question q) {
        QuestionPublic p = new QuestionPublic();
        p.id = q.getId();
        p.subject = q.getSubject();
        p.topic = q.getTopic();
        p.questionText = q.getQuestionText();
        p.optionA = q.getOptionA();
        p.optionB = q.getOptionB();
        p.optionC = q.getOptionC();
        p.optionD = q.getOptionD();
        p.difficultyLabel = q.difficultyLabel();
        return p;
    }
}
