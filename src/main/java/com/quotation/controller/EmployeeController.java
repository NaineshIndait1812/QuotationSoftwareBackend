package com.quotation.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.quotation.model.Employee;
import com.quotation.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/admin/employees")
@CrossOrigin
public class EmployeeController {

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
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
}
