package com.qbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Self-Calibrating Question Bank Engine Using Real-Time Learner Response Analytics.
 *
 * Modules:
 *  1. Response Data Collection Module   -> ResponseController / ResponseService
 *  2. Adaptive Difficulty Calibration   -> CalibrationService (Elo / Rasch-style IRT approximation)
 *  3. Test Composition & Recommendation -> TestCompositionService
 */
@SpringBootApplication
public class QbankApplication {
    public static void main(String[] args) {
        SpringApplication.run(QbankApplication.class, args);
    }
}
