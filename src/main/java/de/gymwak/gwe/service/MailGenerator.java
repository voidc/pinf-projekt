package de.gymwak.gwe.service;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

public class MailGenerator {
	private AsyncMailService mailService;

	@Value("${gwe.email}")
	private String adminMail;

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	public MailGenerator(AsyncMailService mailService) {
		this.mailService = mailService;
	}

	public String getAddress() {
		String serverAddress = "localhost";
		try {
			serverAddress = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement()
					.getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return String.format("http://%s:%s", serverAddress, serverPort);
	}

	/**
	 * @return boolean success
	 */
	public boolean sendActivationMail(GWEUser user, GWERepository userRepository, String token) {
		try {
			user.setActivationToken(token);
			userRepository.save(user);

			String activationUrl = getAddress() + "/gwe/activation?token=" + token;

			mailService.sendMail(mime -> {
				MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
				mail.setSubject("Account aktivieren");
				mail.setFrom(adminMail, "GWE");
				mail.setTo(user.getEmail());
				mail.setText("Ihr Account wurde noch nicht aktiviert. Daher stehen Ihnen einige Funktionen des Portals noch nicht zur Verfügung.<br>"
						+ "Durch Klicken auf den folgenden Link wird Ihre E-Mail-Addresse bestätigt und Sie können anschließend auf alle Funktionen zugreifen.<br>"
						+ "<a href='" + activationUrl + "'>Account aktivieren</a><br>"
						+ "Der Link wird ungültig sobald eine neue Aktivierungs-E-Mail gesendet wurde.", true);
			});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @return boolean success
	 */
	public boolean sendResetMail(GWEUser user, GWERepository userRepository, String token, long tokenExpiry) {
		if (user == null)
			return false;

		user.setResetToken(token);
		user.setResetTokenIssued(new Timestamp(new Date().getTime()));
		userRepository.save(user);

		String resetUrl = getAddress() + "/gwe/reset?token=" + token;
		String exprires = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(user.getResetTokenIssued().getTime() + tokenExpiry));

		mailService.sendMail(mime -> {
			MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
			mail.setSubject("Passwort Zurücksetzen");
			mail.setFrom(adminMail, "GWE");
			mail.setTo(user.getEmail());
			mail.setText("Wir haben eine Anfrage erhalten, dass Sie Ihr Passwort für Ihren Account vergessen haben.<br>"
					+ "Wenn Sie Ihr Passwort nicht zurücksetzen möchten, können Sie diese E-Mail ignorieren.<br>"
					+ "<a href='" + resetUrl + "'>Passwort zurücksetzen</a><br>"
					+ "Der Link ist für 24 Stunden bis " + exprires + " gültig.", true);
		});
		return true;
	}
}
