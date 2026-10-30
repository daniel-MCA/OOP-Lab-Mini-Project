package com.qbank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Module 1: Response Data Collection.
 * One row per question attempt: who answered, what they chose, whether it
 * was correct, and how long it took. This is the raw feed that the
 * CalibrationService consumes to recompute question difficulty.
 */
@Entity
@Table(name = "responses")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "test_attempt_id")
    private TestAttempt testAttempt; // nullable for free practice

    private String selectedOption;
    private boolean correct;
    private long timeTakenMs;

    // Snapshot of ability/difficulty at the moment of the attempt (useful for analytics/audit)
    private double abilityAtAttempt;
    private double difficultyAtAttempt;

    private String mode; // PRACTICE | TEST | TIME_ATTACK

    private LocalDateTime attemptedAt = LocalDateTime.now();

    public Response() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public TestAttempt getTestAttempt() { return testAttempt; }
    public void setTestAttempt(TestAttempt testAttempt) { this.testAttempt = testAttempt; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public long getTimeTakenMs() { return timeTakenMs; }
    public void setTimeTakenMs(long timeTakenMs) { this.timeTakenMs = timeTakenMs; }
    public double getAbilityAtAttempt() { return abilityAtAttempt; }
    public void setAbilityAtAttempt(double abilityAtAttempt) { this.abilityAtAttempt = abilityAtAttempt; }
    public double getDifficultyAtAttempt() { return difficultyAtAttempt; }
    public void setDifficultyAtAttempt(double difficultyAtAttempt) { this.difficultyAtAttempt = difficultyAtAttempt; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
