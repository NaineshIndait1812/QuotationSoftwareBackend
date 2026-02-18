package com.quotation.controller;

import com.quotation.model.Invoice;

import com.quotation.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:5173") // Allow React to connect
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
    
 // Add all invoice 
    @GetMapping("/all-invoices")
    public List<Invoice> getAllInvoices() {
        System.out.println("API Hit: Fetching all invoices..."); // Check your IntelliJ/Eclipse console for this!
        return invoiceRepository.findAll();
    }
    
 // ✅ ADD THIS: Update Invoice Status (For the Edit Button)
    @PutMapping("/update-status/{id}")
    public ResponseEntity<Invoice> updateInvoiceStatus(@PathVariable String id, @RequestBody Map<String, String> statusUpdate) {
        return invoiceRepository.findById(id).map(invoice -> {
            invoice.setStatus(statusUpdate.get("status"));
            return ResponseEntity.ok(invoiceRepository.save(invoice));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable String id) {
        String cleanId = id.trim();
        System.out.println("Attempting to delete ID: [" + cleanId + "]"); 
        
        try {
            if (invoiceRepository.existsById(cleanId)) {
                invoiceRepository.deleteById(cleanId);
                System.out.println("SUCCESS: Invoice deleted.");
                // Correct way to return a JSON message in Spring Boot
                return ResponseEntity.ok().body(Map.of("message", "Deleted successfully"));
            } else {
                System.out.println("FAILURE: ID not found in MongoDB.");
                return ResponseEntity.status(404).body("Invoice not found with ID: " + cleanId);
            }
        } catch (Exception e) {
            System.err.println("Error during delete: " + e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}