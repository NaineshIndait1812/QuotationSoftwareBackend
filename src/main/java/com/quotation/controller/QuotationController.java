package com.quotation.controller;

import com.quotation.model.Quotation;
import com.quotation.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = {
	    "http://localhost:5173", 
	    "https://robt-triumphant-oratorically.ngrok-free.dev"
	})
public class QuotationController {

    @Autowired
    private QuotationService service;

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
            return ResponseEntity.ok("Email sent successfully with attachment!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    

    
 // --- Replace your old approve/reject methods with these two ---

    @GetMapping("/approve")
    public String approve(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);

        if (quotation == null) {
            return "<h2 style='color:red;'>Error: Invalid or Expired Token</h2>";
        }

        if (quotation.isApprovalUsed()) {
            return "<h2 style='color:orange;'>This quotation has already been processed.</h2>";
        }

        // 1. Update DB Status
        quotation.setStatus("Approved");
        quotation.setApprovalUsed(true);
        service.saveQuotation(quotation);

        // 2. Notify the Team (Internal Email)
        service.sendStatusUpdateNotification(quotation);

        return "<div style='text-align:center; padding:50px; font-family:Arial;'>" +
               "<h1 style='color:#16a34a;'>✅ Thank You!</h1>" +
               "<p>The quotation for <b>" + quotation.getProject() + "</b> has been approved successfully.</p>" +
               "</div>";
    }

    @GetMapping("/reject")
    public String reject(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);

        if (quotation == null) {
            return "<h2 style='color:red;'>Error: Invalid Token</h2>";
        }

        // 1. Update DB Status
        quotation.setStatus("Rejected");
        quotation.setApprovalUsed(true);
        service.saveQuotation(quotation);

        // 2. Notify the Team (Internal Email)
        service.sendStatusUpdateNotification(quotation);

        return "<div style='text-align:center; padding:50px; font-family:Arial;'>" +
               "<h1 style='color:#dc2626;'>Quotation Rejected</h1>" +
               "<p>You have rejected the quotation for <b>" + quotation.getProject() + "</b>.</p>" +
               "</div>";
    }
}