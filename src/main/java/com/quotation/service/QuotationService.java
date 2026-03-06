package com.quotation.service;

import com.quotation.model.Quotation;


import java.util.UUID;
import java.time.LocalDateTime;
import com.quotation.repository.QuotationRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public Quotation saveQuotation(Quotation quotation) {
        if (quotation == null) throw new RuntimeException("Quotation data is missing");
        if (quotation.getStatus() == null || quotation.getStatus().trim().isEmpty()) {
            quotation.setStatus("Draft");
        }
        return repository.save(quotation);
    }

    public List<Quotation> getQuotationsForList() {
        return repository.findAllExcludingHeavyFields();
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
        return repository.save(quotation);
    }
    
    public List<Quotation> getQuotationsByEmployee(String empId) {
        return repository.findByPreparedBy(empId);
    }
    
    public Quotation getByToken(String token) {
        return repository.findByApprovalToken(token);
    }

    // THE CLEANED METHOD
    public void sendForApprovalWithPdf(String id, MultipartFile file) {

        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        try {

            // ✅ 1. Generate Token
            String token = UUID.randomUUID().toString();
            quotation.setApprovalToken(token);
            quotation.setApprovalUsed(false);

            repository.save(quotation);   // ✅ Save token in DB

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(quotation.getClientEmail());
            helper.setSubject("Approval Required: " + quotation.getProject());

            // ✅ 2. Use token in URL
            String approveUrl = "http://localhost:8080/api/quotations/approve?token=" + token;
            String rejectUrl  = "http://localhost:8080/api/quotations/reject?token=" + token;

            double displayAmount = quotation.getTotalCost();

if (quotation.getGstPercent() != null && quotation.getGstPercent() > 0) {
    displayAmount = quotation.getFinalAmount();
}

            String emailBody =
                    "<div style='font-family:Arial;padding:20px;'>"
                            + "<h2 style='color:#f97316;'>Quotation Approval Required</h2>"

                            + "<p>Dear " + quotation.getClient() + ",</p>"

                            + "<p><b>Quotation No:</b> " + quotation.getQuotationNumber() + "</p>"
                            + "<p><b>Date:</b> " + quotation.getDate() + "</p>"
                            + "<p><b>Valid Until:</b> " + quotation.getValidUntil() + "</p>"

                            + "<hr/>"

                            + "<p><b>Project:</b> " + quotation.getProject() + "</p>"
                            + "<p><b>Total Cost:</b> ₹ " + displayAmount + "</p>"
                            + "<p><b>Total Timeline:</b> " + quotation.getTotalTimeline() + "</p>"

                            + "<hr/>"

                            + "<p>Please click below:</p>"

                            + "<a href='" + approveUrl + "' "
                            + "style='background:#16a34a;color:white;padding:10px 18px;"
                            + "text-decoration:none;border-radius:6px;margin-right:10px;'>"
                            + "Approve</a>"

                            + "<a href='" + rejectUrl + "' "
                            + "style='background:#dc2626;color:white;padding:10px 18px;"
                            + "text-decoration:none;border-radius:6px;'>"
                            + "Reject</a>"

                            + "<br/><br/>"
                            + "<p>Best Regards,<br/>SmartMatrix Team</p>"
                            + "</div>";

            helper.setText(emailBody, true);

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

            // Set this to your company email (the one that should receive the notification)
            helper.setTo("your-company-email@gmail.com"); 
            helper.setSubject("ALERT: Quotation " + quotation.getStatus() + " - " + quotation.getProject());

            String emailBody = 
                "<div style='font-family:Arial;padding:20px;border:1px solid #eee;'>" +
                "<h2 style='color:#2563eb;'>Status Update Received</h2>" +
                "<p>The client <b>" + quotation.getClient() + "</b> has responded to a quotation.</p>" +
                "<p><b>Quotation No:</b> " + quotation.getQuotationNumber() + "</p>" +
                "<p><b>New Status:</b> <span style='padding:5px 10px;background:#fef3c7;font-weight:bold;'>" + 
                quotation.getStatus() + "</span></p>" +
                "<p><b>Project:</b> " + quotation.getProject() + "</p>" +
                "<br/>" +
                "<p>Check the admin panel for more details.</p>" +
                "</div>";

            helper.setText(emailBody, true);
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send internal notification: " + e.getMessage());
        }
    }
    
}