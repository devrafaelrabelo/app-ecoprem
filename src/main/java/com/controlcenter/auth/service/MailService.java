package com.controlcenter.auth.service;

import com.controlcenter.auth.config.MailProperties;
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

    public void sendAccountLockedEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject("🚫 Your ControlCenter Account Has Been Locked");
        message.setText("Hello " + username + ",\n\n"
                + "We noticed multiple failed login attempts on your account. As a security measure, "
                + "your account has been temporarily locked for 15 minutes.\n\n"
                + "If this wasn't you, please contact our support team immediately.\n\n"
                + "Best regards,\nThe ControleCenter Security Team");

        try {
            mailSender.send(message);
            log.info("📧 E-mail de bloqueio enviado para {}", to);
        } catch (Exception e) {
            log.error("❌ Falha ao enviar e-mail de bloqueio para {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "🔐 Redefinição de senha - ControleCenter";
        String resetUrl = "https://app.bemprotege.com.br/reset-password?token=" + token;

        String body = """
        Olá,

        Recebemos uma solicitação para redefinir sua senha. Para continuar, acesse o link abaixo:
        %s

        Se você não solicitou esta ação, ignore este e-mail.

        Atenciosamente,
        Equipe ControleCenter
        """.formatted(resetUrl);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("📧 E-mail de redefinição de senha enviado para {}", to);
        } catch (Exception e) {
            log.error("❌ Falha ao enviar e-mail de redefinição para {}: {}", to, e.getMessage(), e);
        }
    }
}
