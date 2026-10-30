package com.qbank.service;

import com.qbank.model.Question;
import com.qbank.model.User;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.ResponseRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
// Random is already covered by java.util.* above

/**
 * Module 3: Test Composition & Recommendation Module.
 *
 * Builds balanced, personalized question sets using each item's live,
 * recalibrated difficulty (from CalibrationService) and the learner's
 * current ability estimate — the core "adaptive practice" experience.
 */
@Service
public class TestCompositionService {

    private final QuestionRepository questionRepository;
    private final CalibrationService calibrationService;
    private final ResponseRepository responseRepository;

    public TestCompositionService(QuestionRepository questionRepository, CalibrationService calibrationService,
                                  ResponseRepository responseRepository) {
        this.questionRepository = questionRepository;
        this.calibrationService = calibrationService;
        this.responseRepository = responseRepository;
    }

    /**
     * Adaptive practice: picks ONE next question whose difficulty sits close
     * to the learner's current ability (targets ~60-70% success probability,
     * the sweet spot for learning per adaptive-testing theory), excluding
     * recently-seen questions where possible.
     */
    public Optional<Question> nextAdaptiveQuestion(User user, String subject, String topic, Set<Long> excludeIds) {
        List<Question> pool = questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject);
        if (topic != null && !topic.isBlank()) {
            pool = pool.stream().filter(q -> topic.equalsIgnoreCase(q.getTopic())).collect(Collectors.toList());
        }
        Set<Long> learnerHistory = recentQuestionIds(user, subject);
        pool.removeIf(q -> excludeIds.contains(q.getId()) || learnerHistory.contains(q.getId()));
        if (pool.isEmpty()) {
            // Allow repeats only after unseen items are exhausted — but keep
            // honoring the topic filter if one was requested.
            pool = questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject);
            if (topic != null && !topic.isBlank()) {
                pool = pool.stream().filter(q -> topic.equalsIgnoreCase(q.getTopic())).collect(Collectors.toList());
            }
            pool.removeIf(q -> excludeIds.contains(q.getId()));
        }
        if (pool.isEmpty()) return Optional.empty();

        double ability = user.getAbility(subject);
        // Target difficulty = ability + small positive offset -> a "stretch" question.
        double targetDifficulty = ability + 0.3;

        return Optional.ofNullable(pickWithJitter(pool, targetDifficulty, 3));
    }

    /**
     * Picks a question at random from among the N closest matches to the
     * target difficulty, instead of always the single closest one.
     *
     * Without this, two learners who happen to share the same ability
     * estimate (e.g. two brand-new accounts, both starting at ability = 0)
     * would deterministically get the exact same question every time,
     * which looks like "everyone gets the same question" even though
     * personalization is working correctly — it just has nothing to
     * differentiate on yet. This keeps selection anchored to ability while
     * adding enough variety that fresh/tied accounts don't look identical.
     */
    private Question pickWithJitter(List<Question> candidates, double target, int windowSize) {
        if (candidates.isEmpty()) return null;
        List<Question> sorted = candidates.stream()
                .sorted(Comparator.comparingDouble(q -> Math.abs(q.getDifficulty() - target)))
                .collect(Collectors.toList());
        int n = Math.min(windowSize, sorted.size());
        List<Question> window = sorted.subList(0, n);
        return window.get(new Random().nextInt(window.size()));
    }

    /**
     * Builds a full test: balanced across topics, difficulty spread
     * calibrated to the learner's ability (mostly on-level, some stretch,
     * some confidence-builders), similar to how CATs (computer-adaptive
     * tests) blueprint a form.
     */
    public List<Question> composeTest(User user, String subject, int count) {
        List<Question> pool = questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject);
        if (pool.isEmpty()) return List.of();

        Set<Long> history = recentQuestionIds(user, subject);
        List<Question> unseen = pool.stream().filter(q -> !history.contains(q.getId())).collect(Collectors.toList());
        if (!unseen.isEmpty()) pool = unseen;

        double ability = user.getAbility(subject);

        // Group by topic to keep coverage balanced.
        Map<String, List<Question>> byTopic = pool.stream().collect(Collectors.groupingBy(Question::getTopic));
        List<String> topics = new ArrayList<>(byTopic.keySet());
        Collections.shuffle(topics);

        List<Question> selected = new ArrayList<>();
        int idx = 0;
        Set<Long> used = new HashSet<>();

        while (selected.size() < count && !topics.isEmpty()) {
            String topic = topics.get(idx % topics.size());
            List<Question> candidates = byTopic.get(topic).stream()
                    .filter(q -> !used.contains(q.getId()))
                    .toList();

            if (!candidates.isEmpty()) {
                // Vary target difficulty slightly per slot: 20% easier confidence
                // builder, 60% on-level, 20% stretch — a simple blueprint.
                double roll = Math.random();
                double target = ability + (roll < 0.2 ? -1.0 : roll > 0.8 ? 1.0 : 0.0);

                Question best = pickWithJitter(candidates, target, 3);
                if (best != null) {
                    selected.add(best);
                    used.add(best.getId());
                }
            }
            idx++;
            if (idx > count * 10) break; // safety valve for small banks
        }
        return selected;
    }

    /** Time-Attack: a rapid-fire set biased toward the learner's comfort zone so pace matters more than difficulty. */
    public List<Question> composeTimeAttack(User user, String subject, int count) {
        List<Question> pool = questionRepository.findBySubjectIgnoreCaseAndActiveTrue(subject);
        Set<Long> history = recentQuestionIds(user, subject);
        List<Question> unseen = pool.stream().filter(q -> !history.contains(q.getId())).collect(Collectors.toList());
        if (!unseen.isEmpty()) pool = unseen;
        double ability = user.getAbility(subject);
        return pool.stream()
                .sorted(Comparator.comparingDouble(q -> Math.abs(q.getDifficulty() - (ability - 0.5))))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Set<Long> recentQuestionIds(User user, String subject) {
        Set<Long> ids = new HashSet<>();
        responseRepository.findByUser(user).forEach(r -> {
            if (r.getQuestion() != null && subject.equalsIgnoreCase(r.getQuestion().getSubject()) && r.getQuestion().getId() != null) {
                ids.add(r.getQuestion().getId());
            }
        });
        return ids;
    }

    public CalibrationService getCalibrationService() {
        return calibrationService;
    }
}
