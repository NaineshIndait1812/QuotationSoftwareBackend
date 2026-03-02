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
        Quotation savedQuotation = service.saveQuotation(quotation);

        // 🔔 Notify ADMIN
        String message = "New quotation " + savedQuotation.getQuotationNumber() +
                " created for Client " + savedQuotation.getClient();

        notificationService.createNotification(
                message,
                "ADMIN",
                "QUOTATION_CREATED"
        );

        return savedQuotation;
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

            Quotation quotation = service.getById(id);

            service.sendForApprovalWithPdf(id, file);

            // 🔔 Notify ADMIN
            String message = "Quotation " + quotation.getQuotationNumber() +
                    " sent to client for approval.";

            notificationService.createNotification(
                    message,
                    "ADMIN",
                    "QUOTATION_SENT"
            );

            return ResponseEntity.ok("Email sent successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/approve")
    public String approve(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);
        
        // 1. STRICT LOCK CHECK
        // If quotation is null OR the flag is already true, stop immediately.
        if (quotation == null || quotation.isApprovalUsed()) {
            return renderAlreadyProcessedPage();
        }

        // 2. IMMEDIATE STATE CHANGE
        quotation.setApprovalUsed(true); 
        quotation.setStatus("Approved");
        
        // 3. FORCE SAVE TO DB
        // Ensure your service.saveQuotation uses repository.save() internally
        service.saveQuotation(quotation);

        // 4. NOTIFICATIONS (Happens after DB is locked)
        String msg = "Client " + quotation.getClient() + " approved project: " + quotation.getProject();
        notificationService.createNotification(msg, "ADMIN", "APPROVAL");
        if (quotation.getPreparedBy() != null) {
            notificationService.createNotification(msg, quotation.getPreparedBy(), "APPROVAL");
        }
        service.sendStatusUpdateNotification(quotation);

        return renderSmartPage("Approved Successfully", "✅", "#0369a1", 
        	    "Thank you! Your approval for <b>" + quotation.getProject() + "</b> has been confirmed." +
        	    "Our team has been notified and will proceed with the next steps.");
    }

    @GetMapping("/reject")
    public String reject(@RequestParam String token) {
        Quotation quotation = service.getByToken(token);

        // 1. STRICT LOCK CHECK
        // Even if they click 'Reject' after 'Approve', this flag will now be TRUE
        if (quotation == null || quotation.isApprovalUsed()) {
            return renderAlreadyProcessedPage();
        }

        // 2. IMMEDIATE STATE CHANGE
        quotation.setApprovalUsed(true);
        quotation.setStatus("Rejected");
        
        // 3. FORCE SAVE TO DB
        service.saveQuotation(quotation);

        String msg = "Client " + quotation.getClient() + " rejected project: " + quotation.getProject();
        notificationService.createNotification(msg, "ADMIN", "REJECTION");
        if (quotation.getPreparedBy() != null) {
            notificationService.createNotification(msg, quotation.getPreparedBy(), "REJECTION");
        }
        service.sendStatusUpdateNotification(quotation);

        return renderSmartPage("Quotation Rejected", "❌", "#be123c", 
            "The status has been updated for <b>" + quotation.getProject() + "</b>. We will contact you to discuss further.");
    }

    // --- REUSABLE SMART UI RENDERERS ---

    private String renderSmartPage(String title, String icon, String color, String description) {
        return "<html><body style='margin:0; font-family:sans-serif; background: #f8fafc; display:flex; justify-content:center; align-items:center; height:100vh;'>" +
               "<div style='background:white; padding:50px; border-radius:30px; box-shadow:0 20px 50px rgba(0,0,0,0.08); text-align:center; max-width:450px; border-top: 6px solid " + color + ";'>" +
               "<div style='font-size:60px; margin-bottom:20px;'>" + icon + "</div>" +
               "<h1 style='color:" + color + "; margin:0; font-size:26px;'>" + title + "</h1>" +
               "<p style='color:#475569; line-height:1.6; margin-top:15px; font-size:16px;'>" + description + "</p>" +
               "<div style='margin-top:30px; font-size:11px; color:#94a3b8; letter-spacing:2px; text-transform:uppercase; font-weight:bold;'>Smart Matrix Systems</div>" +
               "</div></body></html>";
    }

    private String renderAlreadyProcessedPage() {
        return "<html><body style='margin:0; font-family:sans-serif; background: #f1f5f9; display:flex; justify-content:center; align-items:center; height:100vh;'>" +
               "<div style='background:white; padding:40px; border-radius:25px; box-shadow:0 10px 30px rgba(0,0,0,0.05); text-align:center; max-width:400px;'>" +
               "<div style='font-size:50px; margin-bottom:15px;'>⚠️</div>" +
               "<h2 style='color:#64748b; margin:0;'>Action Already Taken</h2>" +
               "<p style='color:#94a3b8; margin-top:10px; font-size:14px;'>This quotation has already been processed. No further changes can be made via this link.</p>" +
               "</div></body></html>";
    }
}