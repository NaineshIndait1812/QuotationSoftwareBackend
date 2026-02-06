package com.quotation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.quotation.model.Admin;
import com.quotation.repository.AdminRepository;
import com.quotation.security.JwtUtil;

@Service
public class AuthService {

    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AdminRepository adminRepository, JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.jwtUtil = jwtUtil;
    }

    // 🔐 LOGIN
    public String login(String username, String password) {

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isEmpty()) {
            return null;
        }

        Admin admin = adminOpt.get();

        if (!admin.getPassword().equals(password)) {
            return null;
        }

        // ✅ Generate JWT token
        return jwtUtil.generateToken(admin.getUsername());
    }
}
