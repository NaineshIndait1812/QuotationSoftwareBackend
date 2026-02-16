package com.quotation.controller;

import com.quotation.model.Quotation;
import com.quotation.service.QuotationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile; // ✅ Add this

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost:5173") // React frontend URL
public class QuotationController {

    @Autowired
    private QuotationService service;

    // ------------------- CRUD Operations -------------------
    @PostMapping
    public Quotation saveQuotation(@RequestBody Quotation quotation) {
        return service.saveQuotation(quotation);
    }

    @GetMapping
    public List<Quotation> getAllQuotations() {
        return service.getAllQuotations();
    }

    @GetMapping("/{id}")
    public Quotation getQuotationById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteQuotation(@PathVariable String id) {
        service.deleteQuotation(id);
        return "Quotation deleted successfully";
    }

    // ------------------- Send Email with Existing PDF -------------------
    @PostMapping("/{id}/send-approval-with-pdf")
    public ResponseEntity<String> sendForApprovalWithPdf(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) { // ✅ Now works

        service.sendForApprovalWithPdf(id, file);
        return ResponseEntity.ok("Email sent successfully with PDF");
    }

    // ------------------- Send Email by Generating PDF from HTML -------------------
    @PostMapping("/{id}/send-approval-html")
    public ResponseEntity<String> sendForApprovalHtml(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String htmlContent = body.get("htmlContent");

        service.sendForApprovalWithHtml(id, htmlContent);

        return ResponseEntity.ok("Email sent successfully with generated PDF");
    }
}
