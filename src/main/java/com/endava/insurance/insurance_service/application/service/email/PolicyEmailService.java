package com.endava.insurance.insurance_service.application.service.email;

import com.endava.insurance.insurance_service.application.service.document.PolicyPdfService;
import com.endava.insurance.insurance_service.domain.model.Policy;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyEmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final PolicyPdfService policyPdfService;

    @Value("${insurance.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${insurance.email.from:no-reply@insurance.local}")
    private String sender;

    public void sendActivationEmail(Policy policy) {
        String recipient = policy.getClient().getEmail();
        if (recipient == null || recipient.isBlank()) {
            log.warn("Policy activation email skipped. Client has no email: policyId={}", policy.getId());
            return;
        }

        if (!emailEnabled) {
            log.info("Policy activation email skipped because insurance.email.enabled=false: policyId={}, recipient={}",
                    policy.getId(), recipient);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Policy activation email skipped. JavaMailSender is not configured: policyId={}", policy.getId());
            return;
        }

        try {
            byte[] pdf = policyPdfService.generatePolicyDocument(policy);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(recipient.trim());
            helper.setFrom(sender.trim());
            helper.setSubject("Your insurance policy is active - " + policy.getPolicyNumber());
            helper.setText(buildEmailBody(policy), true);
            helper.addAttachment(
                    "policy-" + policy.getPolicyNumber() + ".pdf",
                    new ByteArrayResource(pdf),
                    "application/pdf"
            );

            mailSender.send(message);
            log.info("Policy activation email sent: policyId={}, recipient={}", policy.getId(), recipient);
        } catch (Exception ex) {
            log.error("Policy activation email could not be sent: policyId={}, recipient={}",
                    policy.getId(), recipient, ex);
        }
    }

    private String buildEmailBody(Policy policy) {
        return """
                <p>Hello %s,</p>
                <p>Your insurance policy <strong>%s</strong> is now active.</p>
                <p>The full policy document is attached as a PDF.</p>
                <p>Thank you,<br/>Insurance Service</p>
                """.formatted(
                escapeHtml(policy.getClient().getName()),
                escapeHtml(policy.getPolicyNumber())
        );
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
