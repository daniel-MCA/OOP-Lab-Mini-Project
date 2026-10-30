package com.qbank.dto;

public class AuthDtos {

    public static class SignupRequest {
        public String username;
        public String password;
        public String fullName;
        public String role; // STUDENT or ADMIN
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class UserResponse {
        public Long id;
        public String username;
        public String fullName;
        public String role;
        public double xp;
        public int streak;

        public UserResponse(Long id, String username, String fullName, String role, double xp, int streak) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
            this.xp = xp;
            this.streak = streak;
        }
    }
}
