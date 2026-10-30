package com.qbank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject; // JAVA, PYTHON, CPP, OS, DSA

    @Column(nullable = false)
    private String topic; // e.g. "OOP", "Collections", "Threads"

    @Lob
    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false) private String optionA;
    @Column(nullable = false) private String optionB;
    @Column(nullable = false) private String optionC;
    @Column(nullable = false) private String optionD;

    @Column(nullable = false)
    private String correctOption; // "A" | "B" | "C" | "D"

    @Lob
    private String explanation;

    // --- Item Response Theory (simplified 2PL Rasch/Elo-style) parameters ---
    // difficulty (b-parameter): higher = harder. Starts at 0 (average) until calibrated.
    private double difficulty = 0.0;
    // discrimination (a-parameter): how sharply this question separates strong/weak learners.
    private double discrimination = 1.0;

    private int timesAttempted = 0;
    private int timesCorrect = 0;

    // Anomaly flag: set true by CalibrationService when a question's live stats
    // deviate sharply from its expected performance (e.g. "easy" question that
    // almost nobody gets right -> possibly mis-keyed).
    private boolean anomalyFlag = false;
    private String anomalyReason;

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Question() {}

    public double getAccuracy() {
        return timesAttempted == 0 ? -1 : (double) timesCorrect / timesAttempted;
    }

    public String difficultyLabel() {
        return difficultyLabelFor(difficulty);
    }

    /** Same banding logic, usable for a historical/snapshotted difficulty value (e.g. difficultyAtAttempt). */
    public static String difficultyLabelFor(double d) {
        if (d < -1.0) return "Easy";
        if (d < 1.0) return "Medium";
        return "Hard";
    }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public double getDifficulty() { return difficulty; }
    public void setDifficulty(double difficulty) { this.difficulty = difficulty; }
    public double getDiscrimination() { return discrimination; }
    public void setDiscrimination(double discrimination) { this.discrimination = discrimination; }
    public int getTimesAttempted() { return timesAttempted; }
    public void setTimesAttempted(int timesAttempted) { this.timesAttempted = timesAttempted; }
    public int getTimesCorrect() { return timesCorrect; }
    public void setTimesCorrect(int timesCorrect) { this.timesCorrect = timesCorrect; }
    public boolean isAnomalyFlag() { return anomalyFlag; }
    public void setAnomalyFlag(boolean anomalyFlag) { this.anomalyFlag = anomalyFlag; }
    public String getAnomalyReason() { return anomalyReason; }
    public void setAnomalyReason(String anomalyReason) { this.anomalyReason = anomalyReason; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
