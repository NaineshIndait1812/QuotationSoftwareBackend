package com.quotation.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.quotation.model.Employee;
import com.quotation.repository.EmployeeRepository;
import com.quotation.security.JwtUtil;

@Service
public class EmployeeAuthService {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;

    public EmployeeAuthService(EmployeeRepository employeeRepository,
                               JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String empId, String password) {

        Optional<Employee> empOpt =
                employeeRepository.findByEmpId(empId);

        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();

            if (emp.getPassword().equals(password)) {
                return jwtUtil.generateToken(empId);
            }
        }
        return null;
    }
}
