package de.gymwak.gwe.service;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AsyncMailService {
    private JavaMailSender mailSender;
    private TokenGenerator tokenGen;
    private GWERepository userRepository;

    @Value("${EMAIL_USER}")
    private String adminMail;

    @Value("${GWE_ADDRESS}")
    private String gweAddress;

    @Value("${gwe.reset-token-expiry}")
    private long tokenExpiry;

    @Autowired
    public AsyncMailService(JavaMailSender mailSender, TokenGenerator tokenGen,
                            GWERepository userRepository) {
        this.mailSender = mailSender;
        this.tokenGen = tokenGen;
        this.userRepository = userRepository;
    }

    @Async
    public void sendMail(MimeMessagePreparator mail) {
        try {
            mailSender.send(mail);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    public void sendActivationMail(GWEUser recipient) {
        String token = tokenGen.nextToken();
        recipient.setActivationToken(token);
        userRepository.save(recipient);

        String activationUrl = gweAddress + "activate?token=" + token;

        sendMail(mime -> {
            MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
            mail.setSubject("Account Aktivieren");
            mail.setFrom(adminMail, "GWE");
            mail.setTo(recipient.getEmail());
            mail.setText(
                    "Ihr Account wurde noch nicht aktiviert. Daher stehen Ihnen einige Funktionen des Portals noch nicht zur Verfügung.<br>"
                            + "Durch Klicken auf den folgenden Link wird Ihre E-Mail-Addresse bestätigt und Sie können anschließend auf alle Funktionen zugreifen.<br>"
                            + "<a href='" + activationUrl + "'>Account aktivieren</a><br>"
                            + "Der Link wird ungültig sobald eine neue Aktivierungs-E-Mail gesendet wurde.", true);
        });
    }

    public boolean sendResetMail(GWEUser recipient) {
        String token = tokenGen.nextToken();
        recipient.setResetToken(token);
        recipient.setResetTokenIssued(new Timestamp(new Date().getTime()));
        userRepository.save(recipient);

        String resetUrl = gweAddress + "reset?token=" + token;
        String exprires = new SimpleDateFormat("dd.MM.yyyy HH:mm")
                .format(new Date(recipient.getResetTokenIssued().getTime() + tokenExpiry));

        sendMail(mime -> {
            MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
            mail.setSubject("Passwort Zurücksetzen");
            mail.setFrom(adminMail, "GWE");
            mail.setTo(recipient.getEmail());
            mail.setText("Wir haben eine Anfrage erhalten, dass Sie Ihr Passwort für Ihren Account vergessen haben.<br>"
                    + "Wenn Sie Ihr Passwort nicht zurücksetzen möchten, können Sie diese E-Mail ignorieren.<br>"
                    + "<a href='" + resetUrl + "'>Passwort zurücksetzen</a><br>" + "Der Link ist für 24 Stunden bis "
                    + exprires + " gültig.", true);
        });
        return true;
    }

}
