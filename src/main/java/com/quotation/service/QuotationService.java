package com.quotation.service;

import com.quotation.model.Quotation;



import java.util.UUID;
import java.time.LocalDateTime;
import com.quotation.repository.QuotationRepository;
import com.quotation.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; 
import java.time.format.DateTimeFormatter; 

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository repository;

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private NotificationService notificationService;

 // Inside QuotationService.java

    @Transactional
    public Quotation saveQuotation(Quotation quotation) {
        if (quotation == null) throw new RuntimeException("Quotation data is missing");
        
        // Fix: If it's a brand new quotation with no status, set to Draft
        if (quotation.getStatus() == null || quotation.getStatus().trim().isEmpty()) {
            quotation.setStatus("Draft");
        }

        // Always update actionDate whenever we save/update
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        quotation.setActionDate(now);

        return repository.save(quotation);
    }

    // Add this helper to ensure consistency when status changes
    public void updateStatusAndSave(Quotation quotation, String newStatus) {
        quotation.setStatus(newStatus);
        
        this.saveQuotation(quotation);
    }
    public List<Quotation> getQuotationsForList() {
        return repository.findAll();
    }

    public Quotation getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteQuotation(String id) {
        repository.deleteById(id);
    }

    public Quotation updateStatus(String id, String newStatus) {
        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));
        quotation.setStatus(newStatus);
        return this.saveQuotation(quotation);
    }
    
    public List<Quotation> getQuotationsByEmployee(String empId) {
        return repository.findByPreparedBy(empId);
    }
    
    public Quotation getByToken(String token) {
        return repository.findByApprovalToken(token);
    }
    
    public void triggerDailyReminders(String empId) {
        List<Quotation> quotations;

        // --- ADDED LOGIC HERE ---
        // If Admin is logged in, we fetch ALL quotations to check for expiration.
        // If an Employee is logged in, we only check their specific quotations.
        if ("ADMIN".equalsIgnoreCase(empId.trim())) {
            quotations = repository.findAll(); 
        } else {
            quotations = repository.findByPreparedBy(empId);
        }
        
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter slashFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        for (Quotation q : quotations) {
            // Only process if status is Pending or already Expired
            if (!"Pending".equalsIgnoreCase(q.getStatus()) && !"Expired".equalsIgnoreCase(q.getStatus())) {
                continue;
            }

            // This unique key prevents duplicate notifications for the same day
            String reminderKey = "REM_DAILY_" + q.getQuotationNumber() + "_" + today;

            // 1. Notify the specific Employee who prepared this quotation
            // Use q.getPreparedBy() to ensure the right employee gets it even if ADMIN triggered the check
            if (!notificationService.existsByKeyAndUser(reminderKey, q.getPreparedBy())) {
                processReminder(q, q.getPreparedBy(), reminderKey, today, slashFormatter);
            }

            // 2. Notify the Admin
            if (!notificationService.existsByKeyAndUser(reminderKey, "ADMIN")) {
                processReminder(q, "ADMIN", reminderKey, today, slashFormatter);
            }
        }
    }

    // Keep your processReminder helper method as it is, it works great!
    private void processReminder(Quotation q, String recipient, String key, LocalDate today, DateTimeFormatter formatter) {
        try {
            String dateStr = q.getValidUntil();
            if (dateStr == null || dateStr.trim().isEmpty()) return;

            LocalDate expiryDate = LocalDate.parse(dateStr.trim(), formatter);

            if (expiryDate.isBefore(today)) {
                String msg = "❌ EXPIRED: Quotation " + q.getQuotationNumber() + " has expired.";
                notificationService.createNotificationWithKey(msg, recipient, "EXPIRED", key);
                
                if (!"Expired".equals(q.getStatus())) {
                    q.setStatus("Expired");
                    this.saveQuotation(q);
                }
            } else {
                String msg = "⏳ REMINDER: Quotation " + q.getQuotationNumber() + " (Client: " + q.getClient() + ") is still pending.";
                notificationService.createNotificationWithKey(msg, recipient, "DAILY_REMINDER", key);
            }
        } catch (Exception e) {
            System.err.println("Reminder Error for " + q.getQuotationNumber() + ": " + e.getMessage());
        }
    }
    
    public void sendForApprovalWithPdf(String id, MultipartFile file) {

        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        try {
        	quotation.setStatus("Pending");
            // This ensures the actionDate is updated to NOW and saved to MongoDB
            this.saveQuotation(quotation);

            // Generate approval token
            String token = UUID.randomUUID().toString();
            quotation.setApprovalToken(token);
            quotation.setApprovalUsed(false);

            this.saveQuotation(quotation);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(quotation.getClientEmail());
            helper.setSubject("Approval Required: " + quotation.getProject());

            String approveUrl = "http://localhost:8080/api/quotations/approve?token=" + token;
            String rejectUrl  = "http://localhost:8080/api/quotations/reject?token=" + token;

            double displayAmount = quotation.getTotalCost();

            if (quotation.getGstPercent() != null && quotation.getGstPercent() > 0) {
                displayAmount = quotation.getFinalAmount();
            }

            String emailBody =
            "<div style='background:#f4f4f4;padding:30px;font-family:Arial,Helvetica,sans-serif;'>"

            + "<div style='max-width:650px;margin:auto;background:white;border-radius:8px;"
            + "box-shadow:0 4px 10px rgba(0,0,0,0.08);overflow:hidden;'>"

            // Header Image
            + "<div style='text-align:center;'>"
            + "<img src='cid:headerImage' style='width:100%;display:block;' alt='SmartMatrix Header' />"
            + "</div>"

            // Content
            + "<div style='padding:30px;'>"

            + "<h2 style='color:#333;margin-top:0;'>Quotation Approval Required</h2>"

            + "<p style='font-size:15px;color:#555;'>Dear <b>" + quotation.getClient() + "</b>,</p>"

            + "<p style='color:#555;'>Please review the quotation details below and approve or reject it.</p>"

            // Quotation Table
            + "<table style='width:100%;border-collapse:collapse;margin-top:20px;'>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Quotation No</b></td>"
            + "<td style='padding:8px 0;color:#333;'>" + quotation.getQuotationNumber() + "</td>"
            + "</tr>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Date</b></td>"
            + "<td style='padding:8px 0;color:#333;'>" + quotation.getDate() + "</td>"
            + "</tr>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Valid Until</b></td>"
            + "<td style='padding:8px 0;color:#333;'>" + quotation.getValidUntil() + "</td>"
            + "</tr>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Project</b></td>"
            + "<td style='padding:8px 0;color:#333;'>" + quotation.getProject() + "</td>"
            + "</tr>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Total Cost</b></td>"
            + "<td style='padding:8px 0;color:#333;font-weight:bold;'>₹ " + displayAmount + "</td>"
            + "</tr>"

            + "<tr>"
            + "<td style='padding:8px 0;color:#666;'><b>Total Timeline</b></td>"
            + "<td style='padding:8px 0;color:#333;'>" + quotation.getTotalTimeline() + "</td>"
            + "</tr>"

            + "</table>"

            // Buttons
            + "<div style='margin-top:35px;text-align:center;'>"

            + "<a href='" + approveUrl + "' "
            + "style='background:#22c55e;color:white;padding:12px 28px;"
            + "text-decoration:none;border-radius:6px;font-weight:bold;"
            + "display:inline-block;margin-right:10px;'>Approve</a>"

            + "<a href='" + rejectUrl + "' "
            + "style='background:#ef4444;color:white;padding:12px 28px;"
            + "text-decoration:none;border-radius:6px;font-weight:bold;"
            + "display:inline-block;'>Reject</a>"

            + "</div>"

            // Closing
            + "<p style='margin-top:30px;color:#555;font-size:14px;'>"
            + "If you have any questions regarding this quotation, feel free to contact us."
            + "</p>"

            + "<p style='margin-top:20px;color:#555;'>"
            + "Best Regards,<br>"
            + "<b>SmartMatrix Digital Services</b>"
            + "</p>"

            + "</div>"

            // Footer Image
            + "<div style='text-align:center;'>"
            + "<img src='cid:footerImage' style='width:100%;display:block;' alt='SmartMatrix Footer' />"
            + "</div>"

            + "</div>"
            + "</div>";

            helper.setText(emailBody, true);

            // Embed header and footer images inline
            ClassPathResource headerRes = new ClassPathResource("images/header.jpg");
            ClassPathResource footerRes = new ClassPathResource("images/footer.jpg");
            helper.addInline("headerImage", headerRes, "image/jpeg");
            helper.addInline("footerImage", footerRes, "image/jpeg");

            helper.addAttachment(file.getOriginalFilename(), file);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
 // Inside QuotationService.java

    public void sendStatusUpdateNotification(Quotation quotation) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Your company email
            helper.setTo("your-company-email@gmail.com"); 
            helper.setSubject("ALERT: Quotation " + quotation.getStatus() + " - " + quotation.getProject());

            // Create a feedback section if the status is Rejected
            String feedbackHtml = "";
            if ("Rejected".equals(quotation.getStatus()) && quotation.getRejectionReason() != null) {
                feedbackHtml = 
                    "<div style='margin-top:20px; padding:15px; background-color:#fff1f2; border-left:5px solid #be123c; border-radius:4px;'>" +
                    "<h4 style='color:#be123c; margin:0 0 10px 0;'>Client Feedback / Rejection Reason:</h4>" +
                    "<p style='color:#475569; font-style:italic; margin:0;'>\"" + quotation.getRejectionReason() + "\"</p>" +
                    "</div>";
            }

            String emailBody = 
                "<div style='font-family:Arial,sans-serif; padding:20px; border:1px solid #eee;'>" +
                "<h2 style='color:#2563eb;'>Quotation Status Update</h2>" +
                "<p>The client <b>" + quotation.getClient() + "</b> has responded to <b>Quotation #" + quotation.getQuotationNumber() + "</b>.</p>" +
                "<p><b>New Status:</b> <span style='padding:4px 8px; background:#fef3c7; border-radius:4px; font-weight:bold;'>" + quotation.getStatus() + "</span></p>" +
                "<p><b>Project:</b> " + quotation.getProject() + "</p>" +
                feedbackHtml + // This inserts the comment if it exists
                "<br/><p>Please check the Admin Dashboard for more details.</p>" +
                "</div>";

            helper.setText(emailBody, true);
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send internal notification: " + e.getMessage());
        }
    }
    
}