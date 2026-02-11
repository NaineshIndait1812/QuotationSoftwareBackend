package com.quotation.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.quotation.model.Employee;
import com.quotation.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employee/profile")
@CrossOrigin
public class EmployeeProfileController {

    private final EmployeeRepository employeeRepository;

    public EmployeeProfileController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ✅ GET PROFILE
    @GetMapping("/{empId}")
    public Employee getEmployeeProfile(@PathVariable String empId) {
        return employeeRepository
                .findByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // ✅ UPDATE PROFILE PHOTO (ADD THIS)
    @PutMapping("/photo/{empId}")
    public Employee updateProfilePhoto(
            @PathVariable String empId,
            @RequestBody Map<String, String> body) {

        Employee emp = employeeRepository
                .findByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        emp.setPhoto(body.get("photo"));
        return employeeRepository.save(emp);
    }

    // ✅ UPDATE BASIC PROFILE INFO
@PutMapping("/update/{empId}")
public Employee updateEmployeeProfile(
        @PathVariable String empId,
        @RequestBody Employee updatedData) {

    Employee emp = employeeRepository
            .findByEmpId(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    emp.setName(updatedData.getName());
    emp.setEmail(updatedData.getEmail());
    emp.setPhone(updatedData.getPhone());

    return employeeRepository.save(emp);
}

}

