package com.quotation.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.quotation.model.Employee;

public interface EmployeeRepository
        extends MongoRepository<Employee, String> {

    Optional<Employee> findByEmpId(String empId);
}
