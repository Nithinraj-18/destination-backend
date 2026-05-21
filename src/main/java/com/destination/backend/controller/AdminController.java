package com.destination.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.destination.backend.dto.ApiResponse;
import com.destination.backend.dto.AuthResponse;
import com.destination.backend.dto.ForgotPasswordRequest;
import com.destination.backend.dto.LoginRequest;
import com.destination.backend.dto.UpdateAdminRequest;
import com.destination.backend.dto.UpdatePasswordRequest;
import com.destination.backend.entity.Admin;
import com.destination.backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<?> createAdmin(@RequestBody LoginRequest request) {

        adminService.createAdmin(request);
        return ResponseEntity.ok(
                new ApiResponse<>("success", "Admin created successfully", null));
    }

    @PutMapping(value = "/update", produces = "application/json")
    public ResponseEntity<?> updateAdmin(@RequestBody UpdateAdminRequest request) {

        String message = adminService.updateAdmin(request);

        return ResponseEntity.ok(
                new ApiResponse<>("success", message, null));
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        AuthResponse authResponse = adminService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>("success", "Login successful", authResponse));
    }

    @PostMapping(value = "/forgot-password", produces = "application/json")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        String message = adminService.forgotPassword(request);
        return ResponseEntity.ok(
                new ApiResponse<>("success", message, null));
    }

    @PostMapping(value = "/update-password", produces = "application/json")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {

        String message = adminService.updatePassword(request);
        return ResponseEntity.ok(
                new ApiResponse<>("success", message, null));
    }

    @GetMapping("/getAll")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAdmin(@RequestParam Long id) {

        String message = adminService.deleteAdmin(id);
        return ResponseEntity.ok(
                new ApiResponse<>("success", message, null));
    }
}
