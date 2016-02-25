package de.gymwak.gwe.mvc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
@RequestMapping("/activation")
public class ActivationController {
	private GWERepository userRepository;
	private AsyncMailService mailService;
	private SecureRandom rnd;

	@Value("${gwe.email}")
	private String adminMail;

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	public ActivationController(GWERepository userRepository, AsyncMailService mailService) {
		this.userRepository = userRepository;
		this.mailService = mailService;
	}

	@PostConstruct
	public void init() {
		rnd = new SecureRandom();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@RequestParam(value = "token", required = false) String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (currentUser.isActivated()) {
			return "redirect:/user";
		}

		if (token == null) {
			return "activation";
		}

		if (verifyToken(token)) {
			currentUser.setActivated(true);
			userRepository.save(currentUser);
			return "redirect:/user";
		} else {
			return "redirect:/activation?error&invalidToken=" + token;
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String sendActivationMail() {
		System.out.println("activation");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());
		if (currentUser == null)
			return "redirect:/reset?error";

		if (sendActivationMail(currentUser, userRepository, mailService, adminMail))
			return "redirect:/activation?success";
		else
			return "redirect:/activation?error";
	}

	private boolean verifyToken(String token) {
		// check if token exists
		GWEUser currentUser = userRepository.findByActivationToken(token);
		if (currentUser == null)
			return false;
		else
			return true;
	}

	public boolean sendActivationMail(GWEUser user, GWERepository userRepository, AsyncMailService mailService, String from) {
		try {
			String token = generateToken(64);
			user.setActivationToken(token);
			userRepository.save(user);

			String activationUrl = getAddress() + "/gwe/activation?token=" + token;

			mailService.sendMail(mime -> {
				MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
				mail.setSubject("Account aktivieren");
				mail.setFrom(from, "GWE");
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

	public String generateToken(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
		    char c = chars[rnd.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
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
}
