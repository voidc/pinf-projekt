package de.gymwak.gwe.mvc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.model.GWEUser.GraduationType;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
@RequestMapping("/signup")
public class SignupController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;
	private AsyncMailService mailService;
	private SecureRandom rnd;

	@Value("${gwe.email}")
	private String adminMail;

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	public SignupController(GWERepository userRepository, PasswordEncoder encoder, AsyncMailService mailService) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.mailService = mailService;
	}

	@PostConstruct
	public void init() {
		rnd = new SecureRandom();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "signup";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String signup(@Valid GWEUser user, BindingResult result, RedirectAttributes rAttr) {
		if (result.hasErrors()) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error";
		}

		if (userRepository.findByEmail(user.getEmail()) != null) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error=email";
		}

		if ((user.getGraduationType() == GraduationType.ABITUR_WALDKRAIBURG && user.getGraduationYear() < 2001)
				|| user.getGraduationYear() < 1940
				|| user.getGraduationYear() > Calendar.getInstance().get(Calendar.YEAR) + 2) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error=year";
		}

		user.setPassword(encoder.encode(user.getPassword()));
		user.setActivated(false);
		user = userRepository.save(user);

		if (!sendActivationMail(user, userRepository, mailService, adminMail))
			return "redirect:/signup?error";

		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		UserDetails userDetails = new User(user.getEmail(), user.getPassword(), authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:/user";

	}

	@ModelAttribute("disciplines")
	public GWEUser.Discipline[] disciplines() {
		return GWEUser.Discipline.values();
	}

	@ModelAttribute("graduationTypes")
	public GWEUser.GraduationType[] graduationTypes() {
		return GWEUser.GraduationType.values();
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
