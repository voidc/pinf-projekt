package de.gymwak.gwe.mvc;

import java.net.NetworkInterface;
import java.net.SocketException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
public class ResetPasswordController {
	private GWERepository userRepository;
	private AsyncMailService mailService;
	private TemplateEngine templateEngine;

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
		if (userRepository.findByEmail(email) == null) {
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
		return "redirect:/resetpassword?action=success";
	}
	
	@RequestMapping(path = "/resetpassword/{resetId}", method = RequestMethod.GET)
	public String reset(@PathVariable String resetId) {
		return "resetpassword";
	}
}
