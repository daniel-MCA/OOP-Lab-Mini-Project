package com.qbank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_attempts")
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private String subject;
    private String mode; // PRACTICE | TEST | TIME_ATTACK

    private int totalQuestions;
    private int correctAnswers;
    private double scorePercent;
    private double xpEarned;

    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private List<Response> responses = new ArrayList<>();

    public TestAttempt() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public double getScorePercent() { return scorePercent; }
    public void setScorePercent(double scorePercent) { this.scorePercent = scorePercent; }
    public double getXpEarned() { return xpEarned; }
    public void setXpEarned(double xpEarned) { this.xpEarned = xpEarned; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public List<Response> getResponses() { return responses; }
    public void setResponses(List<Response> responses) { this.responses = responses; }
}
