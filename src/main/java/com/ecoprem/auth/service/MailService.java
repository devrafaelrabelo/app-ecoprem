package com.ecoprem.auth.service;

import com.ecoprem.auth.config.MailProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /**
     * Envia e-mail de notificação de conta bloqueada por excesso de tentativas de login.
     */
    public void sendAccountLockedEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject("🚫 Your EcoPrem Account Has Been Locked");
        message.setText("Hello " + username + ",\n\n"
                + "We noticed multiple failed login attempts on your account. As a security measure, "
                + "your account has been temporarily locked for 15 minutes.\n\n"
                + "If this wasn't you, please contact our support team immediately.\n\n"
                + "Best regards,\nThe EcoPrem Security Team");

        try {
            mailSender.send(message);
            log.info("📧 E-mail de bloqueio enviado para {}", to);
        } catch (Exception e) {
            log.error("❌ Falha ao enviar e-mail de bloqueio para {}: {}", to, e.getMessage(), e);
        }
    }
}
