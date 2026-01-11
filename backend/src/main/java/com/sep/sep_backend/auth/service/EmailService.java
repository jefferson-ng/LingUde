package com.sep.sep_backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService
 *
 * Responsible for sending emails via SMTP.
 *
 * For local development we use MailHog:
 * - SMTP server: mailhog:1025 (inside Docker) OR localhost:1025 (outside Docker)
 * - MailHog Web UI: http://localhost:8025
 *
 * Later, if you want real emails, you ONLY change the SMTP config (host/port/user/pass),
 * but this class stays the same.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * "From" address used in outgoing emails.
     * We keep it configurable to avoid hardcoding.
     */
    @Value("${app.mail.from:no-reply@sep.local}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends the password reset email.
     *
     * @param toEmail   user email
     * @param resetLink full reset link (contains token)
     */
    public void sendPasswordResetEmail(String toEmail, String resetLink) {

        // Simple text email (easy and enough for now)
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(toEmail);

        message.setSubject("Password reset - SEP Language App");

        // In a later iteration we can make this HTML,
        // but for now plain text is simpler + works perfectly with MailHog.
        message.setText(
                "Hello,\n\n" +
                        "We received a request to reset your password.\n\n" +
                        "Click this link to reset it:\n" +
                        resetLink + "\n\n" +
                        "If you did not request this, you can ignore this email.\n\n" +
                        "Best regards,\n" +
                        "SEP Team"
        );

        // This sends the mail through SMTP (MailHog in dev)
        mailSender.send(message);
    }
}
