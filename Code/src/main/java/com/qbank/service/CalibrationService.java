package com.qbank.service;

import com.qbank.model.Question;
import com.qbank.model.User;
import org.springframework.stereotype.Service;

/**
 * Module 2: Adaptive Difficulty Calibration Module.
 *
 * Implements a live-updating, Elo-inspired approximation of a 1-Parameter
 * Logistic (Rasch) Item Response Theory model:
 *
 *   P(correct) = 1 / (1 + e^-(a * (theta - b)))
 *
 *   where theta = learner ability, b = question difficulty, a = discrimination.
 *
 * After every response, both the learner's ability estimate and the
 * question's difficulty estimate are nudged toward consistency with the
 * observed outcome (classic Elo rating update), so the bank continuously
 * "self-calibrates" from live traffic instead of relying on a fixed manual
 * difficulty label.
 */
@Service
public class CalibrationService {

    // Learning-rate constants (K-factors). Question difficulty moves slower
    // than an individual learner's ability so that one noisy learner can't
    // swing an item's difficulty too fast.
    private static final double K_STUDENT = 0.35;
    private static final double K_QUESTION = 0.15;

    /** Probability the learner answers this question correctly, under the current model. */
    public double expectedProbability(double ability, Question q) {
        double z = q.getDiscrimination() * (ability - q.getDifficulty());
        return 1.0 / (1.0 + Math.exp(-z));
    }

    /**
     * Called after every recorded Response. Updates the learner's ability
     * estimate for the subject and the question's difficulty estimate in
     * opposite directions from the prediction error.
     *
     * @return the ability BEFORE this update (used to snapshot Response.abilityAtAttempt)
     */
    public double calibrate(User user, Question question, boolean correct, long timeTakenMs) {
        double ability = user.getAbility(question.getSubject());
        double expected = expectedProbability(ability, question);
        double actual = correct ? 1.0 : 0.0;
        double error = actual - expected;

        // Update learner ability: got it right when unlikely -> ability rises more.
        double newAbility = ability + K_STUDENT * error;

        // Update question difficulty: opposite sign — a surprising correct
        // answer means the question was probably easier than currently rated.
        double newDifficulty = question.getDifficulty() - K_QUESTION * error;

        // Discrimination: nudged using time-on-task as a weak secondary
        // signal. Very fast correct answers on a "hard" item, or very slow
        // wrong answers on an "easy" item, slightly increase discrimination
        // (the item cleanly separates fast/confident learners from others).
        double avgExpectedSeconds = 45.0;
        double timeSec = Math.max(1, timeTakenMs / 1000.0);
        double speedSignal = (avgExpectedSeconds - timeSec) / avgExpectedSeconds; // >0 = answered fast
        double discriminationDelta = 0.02 * error * speedSignal;
        double newDiscrimination = clamp(question.getDiscrimination() + discriminationDelta, 0.4, 2.5);

        user.setAbility(question.getSubject(), clamp(newAbility, -4, 4));
        question.setDifficulty(clamp(newDifficulty, -4, 4));
        question.setDiscrimination(newDiscrimination);

        question.setTimesAttempted(question.getTimesAttempted() + 1);
        if (correct) question.setTimesCorrect(question.getTimesCorrect() + 1);

        detectAnomaly(question);

        return ability;
    }

    /**
     * Flags a question whose live accuracy is wildly inconsistent with its
     * calibrated difficulty rating — a signal for the Admin Center that the
     * item may be mis-keyed, ambiguous, or mislabeled.
     */
    private void detectAnomaly(Question q) {
        if (q.getTimesAttempted() < 8) {
            q.setAnomalyFlag(false);
            return;
        }
        double accuracy = q.getAccuracy();
        String label = q.difficultyLabel();

        boolean anomalous = false;
        String reason = null;

        if (label.equals("Easy") && accuracy < 0.35) {
            anomalous = true;
            reason = "Rated Easy but only " + Math.round(accuracy * 100) + "% get it right — possibly mis-keyed or ambiguous.";
        } else if (label.equals("Hard") && accuracy > 0.85) {
            anomalous = true;
            reason = "Rated Hard but " + Math.round(accuracy * 100) + "% get it right — difficulty label may be stale.";
        } else if (q.getDiscrimination() < 0.5 && q.getTimesAttempted() > 20) {
            anomalous = true;
            reason = "Very low discrimination (" + String.format("%.2f", q.getDiscrimination())
                    + ") after " + q.getTimesAttempted() + " attempts — question may not distinguish skill levels.";
        }

        q.setAnomalyFlag(anomalous);
        q.setAnomalyReason(reason);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
