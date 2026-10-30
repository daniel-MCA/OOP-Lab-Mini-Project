package com.qbank.controller;

import com.qbank.dto.AuthDtos.*;
import com.qbank.model.User;
import com.qbank.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try {
            User.Role role = "ADMIN".equalsIgnoreCase(req.role) ? User.Role.ADMIN : User.Role.STUDENT;
            User user = authService.signup(req.username, req.password, req.fullName, role);
            return ResponseEntity.ok(toDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User user = authService.login(req.username, req.password);
            return ResponseEntity.ok(toDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(errorBody(e.getMessage()));
        }
    }

    private UserResponse toDto(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name(), u.getXp(), u.getStreak());
    }

    private Object errorBody(String message) {
        return new Object() {
            public final String error = message;
        };
    }
}
