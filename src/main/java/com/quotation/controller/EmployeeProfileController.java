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

    @PutMapping("/update/{empId}")
    public Employee updateEmployee(
        @PathVariable String empId,
        @RequestBody Employee updatedEmp) {

        Employee emp = employeeRepository.findByEmpId(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // ✅ only allowed fields
    emp.setName(updatedEmp.getName());
    emp.setEmail(updatedEmp.getEmail());
    emp.setPhone(updatedEmp.getPhone());

    return employeeRepository.save(emp);
    }

}


