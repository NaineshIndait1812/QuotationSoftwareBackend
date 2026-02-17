package com.quotation.controller;

import com.quotation.model.Invoice;
import com.quotation.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000") // Allow React to connect
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    // ✅ SAVE INVOICE (Called by handleSaveInvoice in React)
    @PostMapping("/create")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return ResponseEntity.ok(savedInvoice);
    }

    // ✅ FETCH INVOICES BY EMPLOYEE ID (Called by fetchMyInvoices in React)
    @GetMapping("/employee/id/{empId}")
    public ResponseEntity<List<Invoice>> getInvoicesByEmployee(@PathVariable String empId) {
        List<Invoice> invoices = invoiceRepository.findByEmployeeId(empId);
        return ResponseEntity.ok(invoices);
    }
}