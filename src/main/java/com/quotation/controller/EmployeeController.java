package com.quotation.controller;

import org.springframework.web.bind.annotation.*;
import com.quotation.model.Employee;
import com.quotation.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/admin/employees")
@CrossOrigin
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }
}
