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
@CrossOrigin(
	    origins = "http://localhost:5173", 
	    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
	    allowedHeaders = "*"
	)
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
    
 // Fetch quotations using the unique Employee ID
    @GetMapping("/employee/id/{empId}")
    public List<Quotation> getQuotationsByEmpId(@PathVariable String empId) {
        return service.getQuotationsByEmployee(empId); 
    }

    // 1. Fixed Status Update (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<Quotation> updateQuotationStatus(
            @PathVariable String id, 
            @RequestBody Map<String, String> updates) {
        
        String newStatus = updates.get("status");
        Quotation updated = service.updateStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    // 2. Fixed Delete (Single method returning ResponseEntity)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable String id) {
        service.deleteQuotation(id);
        return ResponseEntity.noContent().build(); 
    }

    @PostMapping("/{id}/send-approval-with-pdf")
    public ResponseEntity<String> sendForApprovalWithPdf(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        service.sendForApprovalWithPdf(id, file);
        return ResponseEntity.ok("Email sent successfully with PDF");
    }

    @PostMapping("/{id}/send-approval-html")
    public ResponseEntity<String> sendForApprovalHtml(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String htmlContent = body.get("htmlContent");
        service.sendForApprovalWithHtml(id, htmlContent);
        return ResponseEntity.ok("Email sent successfully with generated PDF");
    }
}