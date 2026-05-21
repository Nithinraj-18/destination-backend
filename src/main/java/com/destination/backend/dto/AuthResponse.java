package com.destination.backend.dto;

public class AuthResponse {

    private String token;
    private String role;

    // ✅ EMPTY CONSTRUCTOR (required sometimes)
    public AuthResponse() {
    }

    // ✅ THIS IS WHAT YOU ARE MISSING 🔥
    public AuthResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // GETTERS & SETTERS
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}