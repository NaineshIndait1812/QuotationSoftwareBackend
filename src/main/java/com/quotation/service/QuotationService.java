package com.quotation.service;

import com.quotation.model.Quotation;
import com.quotation.repository.QuotationRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import java.util.List;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository repository;

    @Autowired
    private JavaMailSender mailSender;

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

    // THE CLEANED METHOD
    public void sendForApprovalWithPdf(String id, MultipartFile file) {
        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(quotation.getClientEmail());
            helper.setSubject("Approval Required: " + quotation.getProject());
            
            String emailBody = "Dear " + quotation.getClient() + ",\n\n" +
                               "Please find the attached quotation for your project: " + quotation.getProject() + ".\n" +
                               "Kindly review and provide your approval.\n\n" +
                               "Best Regards,\nSmartMatrix Team";
            
            helper.setText(emailBody);

            // Using the filename from the actual file picked in the browser
            helper.addAttachment(file.getOriginalFilename(), file);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}