//package com.quotation.service;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthService {
//
//    public boolean login(String username, String password) {
//
//        // TEMP: hardcoded admin login
//        if ("admin".equals(username) && "admin123".equals(password)) {
//            return true;
//        }
//
//        return false;
//    }
//}



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

    public String login(String username, String password) {

        Optional<Admin> adminOpt = adminRepository.findByUsername(username);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            if (admin.getPassword().equals(password)) {
                return jwtUtil.generateToken(username);
            }
        }

        return null;
    }
}
