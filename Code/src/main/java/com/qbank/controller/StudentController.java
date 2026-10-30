package com.qbank.controller;

import com.qbank.model.User;
import com.qbank.repository.UserRepository;
import com.qbank.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    public StudentController(UserRepository userRepository, AnalyticsService analyticsService) {
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{userId}/home")
    public ResponseEntity<?> home(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", user.getFullName());
        body.put("xp", user.getXp());
        body.put("streak", user.getStreak());
        body.put("skillMatrix", analyticsService.skillMatrix(user));
        body.put("subjectStats", analyticsService.subjectStats(user));
        body.put("recentAttempts", analyticsService.recentAttempts(user));
        body.put("recommendations", analyticsService.recommendations(user)); // auto-surfaced, no manual request needed
        return ResponseEntity.ok(body);
    }

    /** Recommendation engine on its own, in case a page wants just this. */
    @GetMapping("/{userId}/recommendations")
    public ResponseEntity<?> recommendations(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(analyticsService.recommendations(user));
    }
}
