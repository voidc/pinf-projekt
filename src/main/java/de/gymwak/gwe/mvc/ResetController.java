package de.gymwak.gwe.mvc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
@RequestMapping("/reset")
public class ResetController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;
	private AsyncMailService mailService;
	private SecureRandom rnd;

	@Value("${gwe.email}")
	private String adminMail;
	
	@Value("${gwe.reset-token-expiry}")
	private long resetTokenExpiry; //milliseconds

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	public ResetController(GWERepository userRepository, PasswordEncoder encoder, AsyncMailService mailService) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.mailService = mailService;
	}

	@PostConstruct
	public void init() {
		rnd = new SecureRandom();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@RequestParam(value = "token", required = false) String token) {
		if (token == null)
			return "resetpasswordbymail";

		if (verifyToken(token))
			return "newpassword";
		else
			return "redirect:/reset?error&invalidToken=" + token;
	}

	@RequestMapping(method = RequestMethod.POST, params = { "email" })
	public String sendResetMail(@RequestParam String email) {
		GWEUser resetUser = userRepository.findByEmail(email);
		if (resetUser == null)
			return "redirect:/reset?error";

		String token = generateToken(64);
		resetUser.setResetToken(token);
		resetUser.setResetTokenIssued(new Timestamp(new Date().getTime()));
		userRepository.save(resetUser);

		String resetUrl = getAddress() + "/gwe/reset?token=" + token;
		String exprires = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(resetUser.getResetTokenIssued().getTime() + resetTokenExpiry));

		mailService.sendMail(mime -> {
			MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
			mail.setSubject("Passwort Zurücksetzen");
			mail.setFrom(adminMail, "GWE");
			mail.setTo(resetUser.getEmail());
			mail.setText("Wir haben eine Anfrage erhalten, dass Sie Ihr Passwort für Ihren Account vergessen haben.<br>"
					+ "Wenn Sie Ihr Passwort nicht zurücksetzen möchten, können Sie diese E-Mail ignorieren.<br>"
					+ "<a href='" + resetUrl + "'>Passwort zurücksetzen</a><br>"
					+ "Der Link ist für 24 Stunden bis " + exprires + " gültig.", true);
		});
		return "redirect:/reset?success";
	}

	@RequestMapping(method = RequestMethod.POST, params = { "password", "token" })
	public String resetPassword(@RequestParam String password, @RequestParam String token) {
		/*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (currentUser != null)
			return "redirect:/reset";*/

		GWEUser resetUser = userRepository.findByResetToken(token);
		resetUser.setPassword(encoder.encode(password));
		userRepository.save(resetUser);

		// Log user in
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		UserDetails userDetails = new User(resetUser.getEmail(), resetUser.getPassword(), authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, resetUser.getPassword(),
				authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);

		return "redirect:/user";
	}

	private boolean verifyToken(String token) {
		// check if token exists
		GWEUser resetUser = userRepository.findByResetToken(token);
		if (resetUser == null)
			return false;

		// check if token is not expired (24h)
		long timeDiff = System.currentTimeMillis() - resetUser.getResetTokenIssued().getTime();
		if (timeDiff > resetTokenExpiry)
			return false;

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
