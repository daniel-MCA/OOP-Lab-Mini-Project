package com.qbank.dto;

public class QuestionDtos {

    /** What the STUDENT sees — no correct answer included. */
    public static class QuestionPublic {
        public Long id;
        public String subject;
        public String topic;
        public String questionText;
        public String optionA, optionB, optionC, optionD;
        public String difficultyLabel;
    }

    /** Full record for the ADMIN center. */
    public static class QuestionAdmin {
        public Long id;
        public String subject;
        public String topic;
        public String questionText;
        public String optionA, optionB, optionC, optionD;
        public String correctOption;
        public String explanation;
        public double difficulty;
        public double discrimination;
        public int timesAttempted;
        public int timesCorrect;
        public double accuracy;
        public boolean anomalyFlag;
        public String anomalyReason;
        public boolean active;
    }

    public static class QuestionCreateRequest {
        public String subject;
        public String topic;
        public String questionText;
        public String optionA, optionB, optionC, optionD;
        public String correctOption;
        public String explanation;
    }

    public static class SubmitAnswerRequest {
        public Long userId;
        public Long questionId;
        public String selectedOption;
        public long timeTakenMs;
        public String mode; // PRACTICE | TEST | TIME_ATTACK
        public Long testAttemptId; // optional
    }

    public static class SubmitAnswerResponse {
        // Null (omitted) in TEST mode so nothing leaks via the network response
        // until the exam is finished — see PracticeController.submit().
        public Boolean correct;
        public String correctOption;
        public String explanation;
        public Double newDifficulty;
        public double xpEarned;
        public double newAbility;
    }

    /** One row of a post-exam report: what they answered vs. what was correct, with full context. */
    public static class ReportItem {
        public Long questionId;
        public String topic;
        public String questionText;
        public String optionA, optionB, optionC, optionD;
        public String selectedOption;
        public String correctOption;
        public boolean correct;
        public String explanation;
        public long timeTakenMs;
        public double difficultyAtAttempt;
        public String difficultyLabelAtAttempt;
    }

    /** Full report for one completed test/time-attack attempt. */
    public static class ReportSummary {
        public Long attemptId;
        public String subject;
        public String mode;
        public int totalQuestions;
        public int correctAnswers;
        public double scorePercent;
        public String startedAt;
        public String completedAt;
        public java.util.List<ReportItem> items;
    }
}
