package com.destination.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.destination.backend.dto.AuthResponse;
import com.destination.backend.dto.ForgotPasswordRequest;
import com.destination.backend.dto.LoginRequest;
import com.destination.backend.dto.UpdateAdminRequest;
import com.destination.backend.dto.UpdatePasswordRequest;
import com.destination.backend.entity.Admin;
import com.destination.backend.repository.AdminRepository;
import com.destination.backend.security.JwtUtil;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    public void createAdmin(LoginRequest request) {

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail()); // ✅ important
        admin.setPassword(encoder.encode(request.getPassword()));
        if (request.getEmail().equals("destination56662025@gmail.com")) {
            admin.setRole("ADMIN");
        } else {
            admin.setRole("USER");
        }

        adminRepository.save(admin);
    }

    public String updateAdmin(UpdateAdminRequest request) {

        Admin admin = adminRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Only update if not null
        if (request.getUsername() != null) {
            admin.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            admin.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            admin.setPassword(encoder.encode(request.getPassword()));
        }

        adminRepository.save(admin);

        return "User updated successfully";
    }

    public AuthResponse login(LoginRequest request) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email"));

        if (!encoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid  Password");
        }

        String token = jwtUtil.generateToken(admin.getEmail());
        return new AuthResponse(token, admin.getRole());
    }

    public String updatePassword(UpdatePasswordRequest request) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!encoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }

        admin.setPassword(encoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
        return "Password updated successfully";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email"));

        String tempPassword = generateTempPassword();
        admin.setPassword(encoder.encode(tempPassword));
        adminRepository.save(admin);
        // ✅ Send email
        emailService.sendTempPassword(admin.getEmail(), tempPassword, admin.getUsername());
        return "Temporary password sent to your email";
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public String deleteAdmin(Long id) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        adminRepository.delete(admin);

        return "User deleted successfully";
    }
}
