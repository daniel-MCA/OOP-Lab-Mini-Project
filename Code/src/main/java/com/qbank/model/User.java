package com.qbank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt hash

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    // --- Gamification / ability state ---
    private double xp = 0;
    private int streak = 0;
    private LocalDate lastActiveDate;

    // Overall ability estimate (theta), used by the Elo/IRT calibration engine.
    // 0 = average learner. Positive = above average, negative = below average.
    private double abilityJava = 0;
    private double abilityPython = 0;
    private double abilityCpp = 0;
    private double abilityOS = 0;
    private double abilityDSA = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role { STUDENT, ADMIN }

    public User() {}

    public User(String username, String password, String fullName, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public double getAbility(String subject) {
        if (subject == null) return 0;
        return switch (subject.toUpperCase()) {
            case "JAVA" -> abilityJava;
            case "PYTHON" -> abilityPython;
            case "CPP", "C++" -> abilityCpp;
            case "OS", "OPERATING SYSTEMS" -> abilityOS;
            case "DSA" -> abilityDSA;
            default -> 0;
        };
    }

    public void setAbility(String subject, double value) {
        if (subject == null) return;
        switch (subject.toUpperCase()) {
            case "JAVA" -> abilityJava = value;
            case "PYTHON" -> abilityPython = value;
            case "CPP", "C++" -> abilityCpp = value;
            case "OS", "OPERATING SYSTEMS" -> abilityOS = value;
            case "DSA" -> abilityDSA = value;
            default -> {}
        }
    }

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public double getXp() { return xp; }
    public void setXp(double xp) { this.xp = xp; }
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }
    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }
    public double getAbilityJava() { return abilityJava; }
    public void setAbilityJava(double abilityJava) { this.abilityJava = abilityJava; }
    public double getAbilityPython() { return abilityPython; }
    public void setAbilityPython(double abilityPython) { this.abilityPython = abilityPython; }
    public double getAbilityCpp() { return abilityCpp; }
    public void setAbilityCpp(double abilityCpp) { this.abilityCpp = abilityCpp; }
    public double getAbilityOS() { return abilityOS; }
    public void setAbilityOS(double abilityOS) { this.abilityOS = abilityOS; }
    public double getAbilityDSA() { return abilityDSA; }
    public void setAbilityDSA(double abilityDSA) { this.abilityDSA = abilityDSA; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
