package com.ServeTech.Webapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your OTP for ServeTech Registration");
            
            String htmlContent = "<h3>Welcome to ServeTech</h3>"
                    + "<p>Your OTP for registration is: <strong>" + otp + "</strong></p>"
                    + "<p>Please use this to verify your email address.</p>";

            helper.setText(htmlContent, true); // true indicates HTML

            mailSender.send(message);
            logger.info("OTP email sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
