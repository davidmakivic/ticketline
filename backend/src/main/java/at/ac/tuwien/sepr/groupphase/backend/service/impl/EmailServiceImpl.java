package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class EmailServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String token, String receiver) throws MessagingException {
        LOGGER.info("Sending password reset email to " + receiver);
        String link = "https://localhost:4200/api/users/resetPassword?token=" + escapeHtml(token);

        String html = "<!doctype html><html><head><meta charset='utf-8'></head><body>"
            + "<h2>Passwort zurücksetzen</h2>"
            + "<p>Hallo,</p>"
            + "<p>wir haben eine Anfrage zum Zurücksetzen Ihres Passworts für Ihr <strong>Ticketline</strong>-Konto erhalten.</p>"
            + "<p><a href=\"" + link + "\" style=\"display:inline-block;padding:10px 16px;border-radius:6px;text-decoration:none;border:1px solid #0b74de;\">Passwort zurücksetzen</a></p>"
            + "<p>Der Link ist bis <strong>24 Stunden</strong> gültig.</p>"
            + "<hr>"
            + "<p style='font-size:0.9em;color:#666;'>Falls Sie diese Anfrage nicht gestellt haben, können Sie diese E-Mail ignorieren.</p>"
            + "<p>Mit freundlichen Grüßen<br>Ticketline-Team</p>"
            + "</body></html>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom("ticketlineee@gmail.com");
        helper.setTo(receiver);
        helper.setSubject("Password reset");
        helper.setText(html, true);

        mailSender.send(message);
    }

    private String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
