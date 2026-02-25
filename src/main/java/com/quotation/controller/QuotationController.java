package com.quotation.controller;

import com.quotation.model.Quotation;
import com.quotation.service.QuotationService;
import com.quotation.service.NotificationService; // ADD THIS IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost:5173")
public class QuotationController {

    @Autowired
    private QuotationService service;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public Quotation saveQuotation(@RequestBody Quotation quotation) {
        return service.saveQuotation(quotation);
    }

    @GetMapping
    public List<Quotation> getAllQuotations() {
        return service.getQuotationsForList(); 
    }

    @GetMapping("/{id}")
    public Quotation getQuotationById(@PathVariable String id) {
        return service.getById(id);
    }
    
    @GetMapping("/employee/id/{empId}")
    public List<Quotation> getQuotationsByEmpId(@PathVariable String empId) {
        return service.getQuotationsByEmployee(empId); 
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Quotation> updateQuotationStatus(
            @PathVariable String id, 
            @RequestBody Map<String, String> updates) {
        String newStatus = updates.get("status");
        Quotation updated = service.updateStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable String id) {
        service.deleteQuotation(id);
        return ResponseEntity.noContent().build(); 
    }
    
    @PostMapping("/{id}/send-approval-pdf")
    public ResponseEntity<?> sendApprovalPdf(
        @PathVariable String id, 
        @RequestParam("file") MultipartFile file) {
        try {
            service.sendForApprovalWithPdf(id, file); 
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/approve")
    public String approve(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);
        if (quotation == null || quotation.isApprovalUsed()) return "<h2>Invalid or Expired</h2>";

        quotation.setStatus("Approved");
        quotation.setApprovalUsed(true);
        service.saveQuotation(quotation);

        String msg = "Client " + quotation.getClient() + " approved project: " + quotation.getProject();
        
        notificationService.createNotification(msg, "ADMIN", "APPROVAL");
        if (quotation.getPreparedBy() != null) {
            notificationService.createNotification(msg, quotation.getPreparedBy(), "APPROVAL");
        }

        service.sendStatusUpdateNotification(quotation);
        return "<h1>✅ Approved Successfully</h1>";
    }

    @GetMapping("/reject")
    public String reject(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);
        if (quotation == null) return "<h2>Invalid Token</h2>";

        quotation.setStatus("Rejected");
        quotation.setApprovalUsed(true);
        service.saveQuotation(quotation);

        String msg = "Client " + quotation.getClient() + " rejected project: " + quotation.getProject();
        
        notificationService.createNotification(msg, "ADMIN", "REJECTION");
        if (quotation.getPreparedBy() != null) {
            notificationService.createNotification(msg, quotation.getPreparedBy(), "REJECTION");
        }

        service.sendStatusUpdateNotification(quotation);
        return "<h1>❌ Quotation Rejected</h1>";
    }
}