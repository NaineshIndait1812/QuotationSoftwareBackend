package com.quotation.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

import org.springframework.web.bind.annotation.*;

import com.quotation.model.Employee;
import com.quotation.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/admin/employees")
@CrossOrigin
public class EmployeeController {

    private final EmployeeRepository repo;
    private final BCryptPasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // CREATE
    @PostMapping
    public Employee addEmployee(@RequestBody Employee emp) {
        return repo.save(emp);
    }

    // READ
    @GetMapping
    public List<Employee> getAllEmployees() {
        return repo.findAll();
    }

    // UPDATE
    @PutMapping("/{id}")
    public Employee updateEmployee(
            @PathVariable String id,
            @RequestBody Employee emp) {

        emp.setId(id);
        return repo.save(emp);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable String id) {
        repo.deleteById(id);
    }
    
 // Add this inside EmployeeController.java

    @GetMapping("/id/{empId}")
    public ResponseEntity<Employee> getEmployeeByEmpId(@PathVariable String empId) {
        return repo.findByEmpId(empId)
                   .map(employee -> ResponseEntity.ok(employee))
                   .orElse(ResponseEntity.notFound().build());
    }

    // ✅ RESET PASSWORD ENDPOINT (Secure, No plain text storage)
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable String id) {
        return repo.findById(id).map(employee -> {
            // Generate a secure temporary password
            String temporaryPassword = generateSecurePassword();
            
            // Hash the temporary password using BCrypt
            String hashedPassword = passwordEncoder.encode(temporaryPassword);
            
            // Update the employee's password with the hashed version
            employee.setPassword(hashedPassword);
            repo.save(employee);
            
            // Return the temporary password (plaintext) only once in the response
            // This is secure because we never store the plaintext version in the database
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            response.put("temporaryPassword", temporaryPassword);
            
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ GENERATE SECURE RANDOM PASSWORD (8-10 chars with uppercase, lowercase, numbers, symbols)
    private String generateSecurePassword() {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*";
        String allChars = uppercase + lowercase + numbers + symbols;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));
        
        // Fill the rest with random characters (targeting 8-10 characters total)
        int targetLength = 8 + random.nextInt(3); // 8-10 characters
        for (int i = password.length(); i < targetLength; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password to avoid predictable pattern
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
}
