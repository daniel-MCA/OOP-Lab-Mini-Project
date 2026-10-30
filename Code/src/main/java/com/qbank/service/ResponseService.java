package com.qbank.service;

import com.qbank.model.*;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.ResponseRepository;
import com.qbank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Module 1: Response Data Collection Module.
 * Captures each attempt and immediately feeds it into the calibration engine
 * so difficulty ratings and learner ability update in real time.
 */
@Service
public class ResponseService {

    private final ResponseRepository responseRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CalibrationService calibrationService;

    public ResponseService(ResponseRepository responseRepository, QuestionRepository questionRepository,
                            UserRepository userRepository, CalibrationService calibrationService) {
        this.responseRepository = responseRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.calibrationService = calibrationService;
    }

    public Response recordAttempt(User user, Question question, String selectedOption,
                                   long timeTakenMs, String mode, TestAttempt attempt) {
        boolean correct = question.getCorrectOption().equalsIgnoreCase(selectedOption);

        double abilityBefore = calibrationService.calibrate(user, question, correct, timeTakenMs);

        Response response = new Response();
        response.setUser(user);
        response.setQuestion(question);
        response.setSelectedOption(selectedOption);
        response.setCorrect(correct);
        response.setTimeTakenMs(timeTakenMs);
        response.setAbilityAtAttempt(abilityBefore);
        response.setDifficultyAtAttempt(question.getDifficulty());
        response.setMode(mode);
        response.setTestAttempt(attempt);

        // XP + streak
        double xpGain = correct ? (10 + Math.max(0, question.getDifficulty()) * 5) : 2;
        user.setXp(user.getXp() + xpGain);
        updateStreak(user);

        questionRepository.save(question);
        userRepository.save(user);
        return responseRepository.save(response);
    }

    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        LocalDate last = user.getLastActiveDate();
        if (last == null) {
            user.setStreak(1);
        } else if (last.equals(today)) {
            // already counted today
        } else if (last.equals(today.minusDays(1))) {
            user.setStreak(user.getStreak() + 1);
        } else {
            user.setStreak(1);
        }
        user.setLastActiveDate(today);
    }
}
