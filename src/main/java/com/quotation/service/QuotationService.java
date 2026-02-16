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

// PDF and Image Imports
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import javax.imageio.ImageIO;

@Service
public class QuotationService {

    @Autowired
    private QuotationRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    public Quotation saveQuotation(Quotation quotation) {
        return repository.save(quotation);
    }

    public List<Quotation> getAllQuotations() {
        return repository.findAll();
    }

    public Quotation getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteQuotation(String id) {
        repository.deleteById(id);
    }

    public void sendForApprovalWithPdf(String id, org.springframework.web.multipart.MultipartFile file) {
        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(quotation.getClientEmail());
            helper.setSubject("Quotation Approval - " + quotation.getProject());
            helper.setText("Hello " + quotation.getClient() + ",\n\nPlease find attached your quotation PDF.\n\nThank you,\nSmartMatrix Team");
            helper.addAttachment("Quotation.pdf", new ByteArrayResource(file.getBytes()));
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email", e);
        }
    }

    public void sendForApprovalWithHtml(String id, String htmlContent) {
        Quotation quotation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found"));
        try {
            byte[] pdfBytes = generatePdfFromHtml(htmlContent);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(quotation.getClientEmail());
            helper.setSubject("Quotation Approval - " + quotation.getProject());
            helper.setText("Hello " + quotation.getClient() + ",\n\nPlease find attached your quotation PDF.\n\nThank you,\nSmartMatrix Team");
            helper.addAttachment("Quotation.pdf", new ByteArrayResource(pdfBytes));
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending email with generated PDF", e);
        }
    }

    // Corrected Helper Method
    public byte[] generatePdfFromHtml(String html) {
        ImageIO.scanForPlugins();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            // 1. CLEAN THE HTML:
            // First, ensure all <img ...> tags are closed like <img ... />
            // This regex finds img tags that don't end in '/>' and fixes them.
            String xhtmlCompliant = html.replaceAll("<img([^>]*?)(?<!/)>", "<img$1 />");
            
            // 2. Remove any illegal whitespace in Base64 strings (WebP fix)
            xhtmlCompliant = xhtmlCompliant.replaceAll("\\s+(?=data:image)", "");

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            
            // 3. Use the fixed XHTML string
            builder.withHtmlContent(xhtmlCompliant, ""); 
            
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace(); 
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }
}