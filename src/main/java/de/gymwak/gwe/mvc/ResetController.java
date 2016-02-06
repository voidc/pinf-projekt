package de.gymwak.gwe.mvc;

import java.math.BigInteger;
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
	private SecureRandom rnd;
	private PasswordEncoder encoder;
	private AsyncMailService mailService;

	@Value("${server.port}")
	private String serverPort;
	
	@Value("${gwe.reset-token-expiry}")
	private long resetTokenExpiry; //milliseconds

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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (currentUser != null)
			return "changepwd";

		if (token == null)
			return "resetbymail";

		if (verifyToken(token))
			return "changepwd";
		else
			return "redirect:/reset?error&token=" + token;
	}

	@RequestMapping(method = RequestMethod.POST, params = { "email" })
	public String sendResetMail(@RequestParam String email) {
		GWEUser resetUser = userRepository.findByEmail(email);
		if (resetUser == null)
			return "redirect:/reset?error";

		String token = new BigInteger(130, rnd).toString(32);
		resetUser.setResetToken(token);
		resetUser.setResetTokenIssued(new Timestamp(new Date().getTime()));
		userRepository.save(resetUser);

		String resetUrl = getAddress() + "/reset?token=" + token;
		String exprires = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(resetUser.getResetTokenIssued().getTime() + resetTokenExpiry));

		mailService.sendMail(mime -> {
			MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
			mail.setSubject("Passwort Zurücksetzen");
			mail.setFrom("gwesmtpmail@gmail.com", "GWE");
			mail.setTo(resetUser.getEmail());
			mail.setText("<a href='" + resetUrl + "'>Passwort zurücksetzen</a><br>"
					+ "Gültig bis: " + exprires, true);
		});
		return "redirect:/reset?success";
	}

	@RequestMapping(method = RequestMethod.POST, params = { "password" })
	public String resetPassword(@RequestParam String password) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (currentUser == null)
			return "redirect:/reset?error";

		currentUser.setPassword(encoder.encode(password));
		userRepository.save(currentUser);
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

		// Log user in
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		UserDetails userDetails = new User(resetUser.getEmail(), resetUser.getPassword(), authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, resetUser.getPassword(),
				authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return true;
	}

	private String getAddress() {
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
