package com.qbank.controller;

import com.qbank.dto.QuestionDtos.*;
import com.qbank.model.Question;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.UserRepository;
import com.qbank.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    public AdminController(QuestionRepository questionRepository, UserRepository userRepository,
                            AnalyticsService analyticsService) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
    }

    // --- Question Bank CRUD ---

    @GetMapping("/questions")
    public List<QuestionAdmin> allQuestions(@RequestParam(required = false) String subject) {
        List<Question> questions = (subject == null || subject.isBlank())
                ? questionRepository.findAll()
                : questionRepository.findBySubjectIgnoreCase(subject);
        return questions.stream().map(this::toAdmin).collect(Collectors.toList());
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionCreateRequest req) {
        Question q = new Question();
        q.setSubject(req.subject);
        q.setTopic(req.topic);
        q.setQuestionText(req.questionText);
        q.setOptionA(req.optionA);
        q.setOptionB(req.optionB);
        q.setOptionC(req.optionC);
        q.setOptionD(req.optionD);
        q.setCorrectOption(req.correctOption);
        q.setExplanation(req.explanation);
        // New items start at neutral difficulty/discrimination and self-calibrate from live data.
        q.setDifficulty(0.0);
        q.setDiscrimination(1.0);
        questionRepository.save(q);
        return ResponseEntity.ok(toAdmin(q));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionCreateRequest req) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();
        q.setSubject(req.subject);
        q.setTopic(req.topic);
        q.setQuestionText(req.questionText);
        q.setOptionA(req.optionA);
        q.setOptionB(req.optionB);
        q.setOptionC(req.optionC);
        q.setOptionD(req.optionD);
        q.setCorrectOption(req.correctOption);
        q.setExplanation(req.explanation);
        questionRepository.save(q);
        return ResponseEntity.ok(toAdmin(q));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        Question q = questionRepository.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();
        q.setActive(false); // soft delete so historical Response rows stay valid
        questionRepository.save(q);
        return ResponseEntity.ok().build();
    }

    // --- Command center summary ---

    @GetMapping("/summary")
    public Object summary() {
        long totalQuestions = questionRepository.count();
        long totalStudents = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("STUDENT")).count();
        List<Question> anomalies = analyticsService.anomalies();

        return new Object() {
            public final long questions = totalQuestions;
            public final long students = totalStudents;
            public final int anomalyCount = anomalies.size();
        };
    }

    @GetMapping("/anomalies")
    public List<QuestionAdmin> anomalies() {
        return analyticsService.anomalies().stream().map(this::toAdmin).collect(Collectors.toList());
    }

    @GetMapping("/students")
    public List<Object> students() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == com.qbank.model.User.Role.STUDENT)
                .map(u -> new Object() {
                    public final Long id = u.getId();
                    public final String fullName = u.getFullName();
                    public final String username = u.getUsername();
                    public final double xp = u.getXp();
                    public final int streak = u.getStreak();
                    public final double javaAbility = u.getAbility("JAVA");
                    public final double pythonAbility = u.getAbility("PYTHON");
                    public final double cppAbility = u.getAbility("CPP");
                    public final double osAbility = u.getAbility("OS");
                    public final double dsaAbility = u.getAbility("DSA");
                }).collect(Collectors.toList());
    }

    private QuestionAdmin toAdmin(Question q) {
        QuestionAdmin a = new QuestionAdmin();
        a.id = q.getId();
        a.subject = q.getSubject();
        a.topic = q.getTopic();
        a.questionText = q.getQuestionText();
        a.optionA = q.getOptionA();
        a.optionB = q.getOptionB();
        a.optionC = q.getOptionC();
        a.optionD = q.getOptionD();
        a.correctOption = q.getCorrectOption();
        a.explanation = q.getExplanation();
        a.difficulty = q.getDifficulty();
        a.discrimination = q.getDiscrimination();
        a.timesAttempted = q.getTimesAttempted();
        a.timesCorrect = q.getTimesCorrect();
        a.accuracy = q.getAccuracy();
        a.anomalyFlag = q.isAnomalyFlag();
        a.anomalyReason = q.getAnomalyReason();
        a.active = q.isActive();
        return a;
    }
}
