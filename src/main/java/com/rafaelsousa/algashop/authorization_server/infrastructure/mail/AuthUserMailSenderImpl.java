package com.rafaelsousa.algashop.authorization_server.infrastructure.mail;

import com.rafaelsousa.algashop.authorization_server.application.user.UserAccountProperties;
import com.rafaelsousa.algashop.authorization_server.application.user.mail.AuthUserMailSender;
import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUser;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUserMailSenderImpl implements AuthUserMailSender {
    private final JavaMailSender javaMailSender;
    private final UserAccountProperties userAccountProperties;

    @Override
    public void sendActivationEmail(AuthUser user, String activationToken) {
        String subject = "AlgaShop - Active your account";
        String formatedDuration = formatDurantion(userAccountProperties.getToken().getActivationTtl());
        URI link = buildLink(activationToken);

        String body = """
                Hello %s,
               \s
                Please activate your account by clicking the link below:
               \s
                %s
               \s
                This link will expire in %s.
               \s
                If you did not request this, please ignore this email.
               \s
                Thank you,
                AlgaShop Team
       \s""".formatted(user.getName(), link, formatedDuration);

        send(user.getEmail(), subject, body);
    }

    @Override
    public void sendPasswordChangeEmail(AuthUser user, String activationToken) {
        String subject = "AlgaShop - Change your password";
        String formatedDuration = formatDurantion(userAccountProperties.getToken().getPasswordResetTtl());
        URI link = buildLink(activationToken);

        String body = """
                Hello %s,
               \s
                Please set your password by clicking the link below:
               \s
                %s
               \s
                This link will expire in %s.
               \s
                If you did not request this, please ignore this email.
               \s
                Thank you,
                AlgaShop Team
       \s""".formatted(user.getName(), link, formatedDuration);

        send(user.getEmail(), subject, body);
    }

    private String formatDurantion(Duration activationTtl) {
        long hours = activationTtl.toHours();

        if (hours > 0) {
            return hours + " hour(s)";
        }

        return activationTtl.toMinutes() + " minute(s)";
    }

    private URI buildLink(String activationToken) {
        return UriComponentsBuilder.fromUriString(userAccountProperties.getMail().getPasswordChangeUrl())
                .queryParam("token", activationToken)
                .build()
                .toUri();
    }

    private void send(String to, String subject, String body) {
        try {
            log.info("Sending email to {} with subject: {}", to, subject);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.setFrom(userAccountProperties.getMail().getFrom());

            javaMailSender.send(message);
        } catch (Exception ex) {
            log.error("Error when sending email to {}: {}", to, ex.getMessage(), ex);
        }
    }
}
