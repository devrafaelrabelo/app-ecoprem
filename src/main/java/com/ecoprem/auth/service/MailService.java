package com.ecoprem.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendAccountLockedEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("🚫 Your EcoPrem Account Has Been Locked");
        message.setText("Hello " + username + ",\n\n"
                + "We noticed multiple failed login attempts on your account. As a security measure, "
                + "your account has been temporarily locked for 15 minutes.\n\n"
                + "If this wasn't you, please contact our support team immediately.\n\n"
                + "Best regards,\nThe EcoPrem Security Team");

        mailSender.send(message);
    }
}
