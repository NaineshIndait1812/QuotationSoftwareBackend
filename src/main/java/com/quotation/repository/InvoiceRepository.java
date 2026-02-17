package com.quotation.repository;

import com.quotation.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    // This allows you to fetch only invoices created by a specific employee
    List<Invoice> findByEmployeeId(String employeeId);
}