package com.qbank.service;

import com.qbank.model.Question;
import com.qbank.model.Response;
import com.qbank.model.User;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.ResponseRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    private static final List<String> SUBJECTS = Arrays.asList("JAVA", "PYTHON", "CPP", "OS", "DSA");

    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;

    public AnalyticsService(QuestionRepository questionRepository, ResponseRepository responseRepository) {
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
    }

    public Map<String, Integer> skillMatrix(User user) {
        Map<String, Integer> matrix = new LinkedHashMap<>();
        List<Response> responses = responseRepository.findByUserOrderByAttemptedAtDesc(user);

        for (String subject : SUBJECTS) {
            int attempts = 0;
            int correct = 0;
            for (Response r : responses) {
                if (subject.equalsIgnoreCase(r.getQuestion().getSubject())) {
                    attempts++;
                    if (r.isCorrect()) correct++;
                }
            }
            int pct = attempts == 0 ? 0 : (int) Math.round(correct * 100.0 / attempts);
            matrix.put(subject, pct);
        }
        return matrix;
    }

    public List<Map<String, Object>> subjectStats(User user) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Response> responses = responseRepository.findByUserOrderByAttemptedAtDesc(user);

        for (String subject : SUBJECTS) {
            int attempts = 0, correct = 0;
            long time = 0;
            double difficulty = 0;
            Set<Long> uniqueQuestions = new HashSet<>();

            for (Response r : responses) {
                if (subject.equalsIgnoreCase(r.getQuestion().getSubject())) {
                    attempts++;
                    if (r.isCorrect()) correct++;
                    time += r.getTimeTakenMs();
                    difficulty += r.getDifficultyAtAttempt();
                    if (r.getQuestion().getId() != null) uniqueQuestions.add(r.getQuestion().getId());
                }
            }

            long totalQuestions = questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject).size();
            int accuracy = attempts == 0 ? 0 : (int) Math.round(correct * 100.0 / attempts);
            int completion = totalQuestions == 0 ? 0 :
                    (int) Math.min(100, Math.round(uniqueQuestions.size() * 100.0 / totalQuestions));
            double avgTime = attempts == 0 ? 0 : time / (double) attempts / 1000.0;
            double avgDifficulty = attempts == 0 ? 0 : difficulty / attempts;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("subject", subject);
            item.put("attempts", attempts);
            item.put("correct", correct);
            item.put("accuracy", accuracy);
            item.put("completion", completion);
            item.put("averageTimeSeconds", round(avgTime));
            item.put("averageDifficulty", round(avgDifficulty));
            item.put("totalQuestions", totalQuestions);
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> studentAnalytics(User user) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("subjectStats", subjectStats(user));
        out.put("skillMatrix", skillMatrix(user));
        out.put("recentAttempts", recentAttempts(user));
        return out;
    }

    public List<Map<String, Object>> recentAttempts(User user) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Response> responses = responseRepository.findByUserOrderByAttemptedAtDesc(user);
        int limit = Math.min(12, responses.size());

        for (int i = 0; i < limit; i++) {
            Response r = responses.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("subject", r.getQuestion().getSubject());
            item.put("topic", r.getQuestion().getTopic());
            item.put("correct", r.isCorrect());
            item.put("timeSeconds", round(r.getTimeTakenMs() / 1000.0));
            item.put("difficulty", round(r.getDifficultyAtAttempt()));
            item.put("date", r.getAttemptedAt());
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> difficultyDistribution(String subject) {
        List<Question> questions = questionRepository.findBySubjectIgnoreCase(subject);
        long easy = questions.stream().filter(q -> q.difficultyLabel().equals("Easy")).count();
        long medium = questions.stream().filter(q -> q.difficultyLabel().equals("Medium")).count();
        long hard = questions.stream().filter(q -> q.difficultyLabel().equals("Hard")).count();
        double avgDifficulty = questions.stream().mapToDouble(Question::getDifficulty).average().orElse(0);
        double avgAccuracy = questions.stream().filter(q -> q.getAccuracy() >= 0)
                .mapToDouble(Question::getAccuracy).average().orElse(0);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("subject", subject);
        result.put("totalQuestions", questions.size());
        result.put("easy", easy);
        result.put("medium", medium);
        result.put("hard", hard);
        result.put("avgDifficulty", round(avgDifficulty));
        result.put("avgAccuracy", round(avgAccuracy * 100));
        return result;
    }

    public List<Question> anomalies() {
        return questionRepository.findByAnomalyFlagTrue();
    }

    /**
     * MODULE 3 EXTENSION: Recommendation Engine.
     * -------------------------------------------
     * Turns raw response history into an actionable study plan:
     * for every (subject, topic) the learner has questions available in,
     * classifies their mastery as Strong / Developing / Weak / Unseen based
     * on real accuracy, then — for anything short of Strong — recommends a
     * specific target difficulty and pulls actual next questions at that
     * difficulty, preferring ones the learner hasn't seen yet.
     *
     * This is what turns "Study Java" into "Java → Exception Handling →
     * Weak (33% accuracy) → practice these 3 Easy-to-Medium questions".
     */
    public Map<String, Object> recommendations(User user) {
        List<Response> responses = responseRepository.findByUserOrderByAttemptedAtDesc(user);

        // Group all active questions by "SUBJECT||TOPIC" so we cover topics
        // the learner hasn't touched yet, not just ones they've attempted.
        Map<String, List<Question>> byTopic = new LinkedHashMap<>();
        for (String subject : SUBJECTS) {
            for (Question q : questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject)) {
                byTopic.computeIfAbsent(subject + "||" + q.getTopic(), k -> new ArrayList<>()).add(q);
            }
        }

        List<Map<String, Object>> strong = new ArrayList<>();
        List<Map<String, Object>> developing = new ArrayList<>();
        List<Map<String, Object>> weak = new ArrayList<>();
        List<Map<String, Object>> unseen = new ArrayList<>();

        for (Map.Entry<String, List<Question>> entry : byTopic.entrySet()) {
            String[] parts = entry.getKey().split("\\|\\|", 2);
            String subject = parts[0];
            String topic = parts[1];
            List<Question> topicQuestions = entry.getValue();

            int attempts = 0, correct = 0;
            Set<Long> attemptedIds = new HashSet<>();
            for (Response r : responses) {
                Question q = r.getQuestion();
                if (subject.equalsIgnoreCase(q.getSubject()) && topic.equalsIgnoreCase(q.getTopic())) {
                    attempts++;
                    if (r.isCorrect()) correct++;
                    attemptedIds.add(q.getId());
                }
            }

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("subject", subject);
            row.put("topic", topic);
            row.put("attempts", attempts);

            if (attempts == 0) {
                row.put("status", "UNSEEN");
                row.put("statusLabel", "🔴 Not started");
                addRecommendedQuestions(row, user, subject, topic, topicQuestions, attemptedIds, user.getAbility(subject));
                unseen.add(row);
                continue;
            }

            int accuracy = (int) Math.round(correct * 100.0 / attempts);
            row.put("accuracy", accuracy);

            double ability = user.getAbility(subject);
            if (accuracy < 50) {
                row.put("status", "WEAK");
                row.put("statusLabel", "🔴 Needs practice");
                // Confidence-builder: aim noticeably easier than their current ability.
                addRecommendedQuestions(row, user, subject, topic, topicQuestions, attemptedIds, ability - 0.75);
                weak.add(row);
            } else if (accuracy < 75) {
                row.put("status", "DEVELOPING");
                row.put("statusLabel", "🟡 Developing");
                addRecommendedQuestions(row, user, subject, topic, topicQuestions, attemptedIds, ability);
                developing.add(row);
            } else {
                row.put("status", "STRONG");
                row.put("statusLabel", "🟢 Strong");
                strong.add(row);
            }
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("strong", strong);
        out.put("developing", developing);
        out.put("weak", weak);
        out.put("unseen", unseen);
        return out;
    }

    /** Attaches a target difficulty label + 3 concrete next questions (unseen-first) to a recommendation row. */
    private void addRecommendedQuestions(Map<String, Object> row, User user, String subject, String topic,
                                          List<Question> topicQuestions, Set<Long> attemptedIds, double targetDifficulty) {
        List<Question> pool = topicQuestions.stream()
                .filter(q -> !attemptedIds.contains(q.getId()))
                .collect(java.util.stream.Collectors.toList());
        if (pool.isEmpty()) pool = topicQuestions; // everything in the topic has been seen — allow repeats

        List<Question> picks = pool.stream()
                .sorted(Comparator.comparingDouble(q -> Math.abs(q.getDifficulty() - targetDifficulty)))
                .limit(3)
                .collect(java.util.stream.Collectors.toList());

        row.put("recommendedDifficultyLabel", picks.isEmpty() ? "Medium" : picks.get(0).difficultyLabel());
        row.put("recommendedDifficulty", round(targetDifficulty));
        row.put("recommendedQuestions", picks.stream().map(q -> {
            Map<String, Object> qm = new LinkedHashMap<>();
            qm.put("id", q.getId());
            qm.put("questionText", q.getQuestionText());
            qm.put("difficultyLabel", q.difficultyLabel());
            return qm;
        }).collect(java.util.stream.Collectors.toList()));
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}
