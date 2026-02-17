package com.quotation.service;

import com.quotation.model.Quotation;
import com.quotation.repository.QuotationRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.imageio.ImageIO;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    public Quotation saveQuotation(Quotation quotation) {
        if (quotation == null) {
            throw new RuntimeException("Quotation data is missing");
        }
        
        // Use a safer check for status
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
    
 // Add this inside your QuotationService class
    public List<Quotation> getQuotationsByEmployee(String empId) {
        // This calls the findByPreparedBy method you just added to the Repository
        return repository.findByPreparedBy(empId);
    }

    public void sendForApprovalWithPdf(String id, org.springframework.web.multipart.MultipartFile file) {
        Quotation quotation = getById(id);
        if (quotation == null) throw new RuntimeException("Quotation not found");
        
        try {
            sendMailWithAttachment(quotation, file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendForApprovalWithHtml(String id, String htmlContent) {
        Quotation quotation = getById(id);
        if (quotation == null) throw new RuntimeException("Quotation not found");
        
        try {
            byte[] pdfBytes = generatePdfFromHtml(htmlContent);
            sendMailWithAttachment(quotation, pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email with generated PDF", e);
        }
    }

    private void sendMailWithAttachment(Quotation quotation, byte[] attachmentBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(quotation.getClientEmail());
        helper.setSubject("Quotation Approval - " + quotation.getProject());
        helper.setText("Hello " + quotation.getClient() + ",\n\nPlease find attached your quotation PDF.\n\nThank you,\nSmartMatrix Team");
        helper.addAttachment("Quotation.pdf", new ByteArrayResource(attachmentBytes));
        
        mailSender.send(message);
    }

    public byte[] generatePdfFromHtml(String html) {
        ImageIO.scanForPlugins();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String xhtmlCompliant = html.replaceAll("<img([^>]*?)(?<!/)>", "<img$1 />")
                                        .replaceAll("\\s+(?=data:image)", "");

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(xhtmlCompliant, ""); 
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }
}