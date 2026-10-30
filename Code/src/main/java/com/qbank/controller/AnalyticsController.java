package com.qbank.controller;

import com.qbank.model.User;
import com.qbank.repository.UserRepository;
import com.qbank.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private static final List<String> SUBJECTS = Arrays.asList("JAVA", "PYTHON", "CPP", "OS", "DSA");

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService, UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/difficulty")
    public List<Map<String, Object>> difficulty() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String subject : SUBJECTS) {
            result.add(analyticsService.difficultyDistribution(subject));
        }
        return result;
    }

    @GetMapping("/difficulty/{subject}")
    public Map<String, Object> difficultyForSubject(@PathVariable String subject) {
        return analyticsService.difficultyDistribution(subject);
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<?> studentAnalytics(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(analyticsService.studentAnalytics(user));
    }

    @GetMapping("/skill-matrix/{userId}")
    public ResponseEntity<?> skillMatrix(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("skillMatrix", analyticsService.skillMatrix(user));
        return ResponseEntity.ok(body);
    }
}
