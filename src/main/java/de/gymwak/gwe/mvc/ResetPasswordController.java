package de.gymwak.gwe.mvc;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.model.GWEUserResetKey;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
public class ResetPasswordController {
	private GWERepository userRepository;
	private AsyncMailService mailService;
	private TemplateEngine templateEngine;
	
	// Temporäre Lösung zur Speicherung anstelle einer Datenbank (base capacity of 10 sollte ausreichen)
	private ArrayList<GWEUserResetKey> resetKeys = new ArrayList<GWEUserResetKey>();

	@Value("${server.port}")
	private String serverPort;

	@Autowired
	public ResetPasswordController(GWERepository userRepository, AsyncMailService mailService, TemplateEngine templateEngine) {
		this.userRepository = userRepository;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
	}

	@RequestMapping(path = "/resetpassword", method = RequestMethod.GET)
	public String get() {
		return "resetpassword";
	}

	@RequestMapping(path = "/resetpassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam String email) {
		GWEUser user = userRepository.findByEmail(email);
		
		if (user == null) {
			return "redirect:/resetpassword?error";
		}
		/*
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser recipientUser = userRepository.findByEmail(email);

		String ruName = recipientUser.getFirstName() + " " + recipientUser.getLastName();

		String serverAddress = "localhost";
		try {
			serverAddress = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement()
					.getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String resetUrl = String.format("http://%s:%s/resetpassword/%d", serverAddress, serverPort, resetId);
		
		String messageContent = "";
		
		Context ctx = new Context();
		ctx.setVariable("recipient", ruName);
		ctx.setVariable("message", messageContent);
		ctx.setVariable("replyUrl", resetUrl);
		String html = templateEngine.process("email", ctx);

		mailService.sendMail(mime -> {
			MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
			mail.setSubject("Nachricht von " + cuName);
			mail.setFrom("gwesmtpmail@gmail.com", "GWE");
			mail.addTo(recipientUser.getEmail());
			mail.setText(html, true);
		});
		*/
		GWEUserResetKey userResetKey = new GWEUserResetKey();
		String resetKeyString = GWEUserResetKey.generateRandomString(128);
		userResetKey.setResetId(user.getId());
		userResetKey.setExpireTime(GWEUserResetKey.calculateExpireTime());
		userResetKey.setResetKey(resetKeyString);
		resetKeys.add(userResetKey);
		// Temporäres Print-Out zu Debug-Zwecken
		System.out.println(resetKeyString);
		
		return "redirect:/resetpassword?action=success";
	}
	
	@RequestMapping(path = "/resetpassword/{resetKey}", method = RequestMethod.GET)
	public ModelAndView reset(@PathVariable String resetKey) {
		GWEUserResetKey userResetKey = null;
		
		for (GWEUserResetKey k : resetKeys) {
			if (k.getResetKey().equals(resetKey)) {
				userResetKey = k;
			}
		}
		
		if (userResetKey == null) {
			return new ModelAndView("redirect:/resetpassword");
		}
		
		if (userRepository.findOne(userResetKey.getResetId()) == null || userResetKey.isExpired()) {
			resetKeys.remove(userResetKey);
			return new ModelAndView("redirect:/resetpassword?error");
		}
		
		ModelAndView mav = new ModelAndView("newpassword");
		mav.addObject("user", userRepository.findOne(userResetKey.getResetId()));
		return mav;
	}
}
