//package com.quotation.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.quotation.dto.LoginRequest;
//import com.quotation.service.AuthService;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin
//public class AuthController {
//
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
//
//        Map<String, String> response = new HashMap<>();
//
//        boolean success = authService.login(request.getUsername(), request.getPassword());
//
//        if (success) {
//            response.put("message", "Login successful");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("message", "Invalid username or password");
//            return ResponseEntity.status(401).body(response);
//        }
//    }
//}


package com.quotation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quotation.dto.LoginRequest;
import com.quotation.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (token != null) {
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401)
                .body(Map.of("message", "Invalid username or password"));
    }
}
