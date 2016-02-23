package de.gymwak.gwe.mvc;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEMessage;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.AsyncMailService;

@Controller
@EnableAsync
public class MessageController {
	private GWERepository userRepository;
	private AsyncMailService mailService;
	private TemplateEngine templateEngine;

	@Value("${server.port}")
	private String serverPort;

	@Value("${gwe.email}")
	private String adminMail;

	@Autowired
	public MessageController(GWERepository userRepository, AsyncMailService mailService,
			TemplateEngine templateEngine) {
		this.userRepository = userRepository;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
	}

	@RequestMapping(value = "/message", method = RequestMethod.GET)
	public ModelAndView get(@RequestParam String to) {
		ModelAndView mav = new ModelAndView("message");
		if (to.startsWith("year")) {
			mav.addObject("year", to.substring(4));
		} else {
			long recipientId = Long.parseLong(to);
			GWEUser recipient = userRepository.findOne(recipientId);
			mav.addObject("recipient", recipient);
		}
		return mav;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendMessage(@Valid GWEMessage gweMessage) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		List<GWEUser> recipients;
		String recipientSalutation;
		if (gweMessage.getRecipientId() != -1) {
			GWEUser recipientUser = userRepository.findOne(gweMessage.getRecipientId());
			recipients = Collections.singletonList(recipientUser);
			recipientSalutation = recipientUser.getFirstName() + " " + recipientUser.getLastName();
		} else {
			recipients = userRepository.findByGraduationYear(gweMessage.getRecipientsYear());
//			recipientSalutation = "Ehemahliger Schüler des Abiturjahres " + gweMessage.getRecipientsYear();
			recipientSalutation = null;
		}

		String cuName = currentUser.getFirstName() + " " + currentUser.getLastName();
		String replyUrl = String.format(getAddress() + "/message?to=%d", currentUser.getId());
		String messageContent = gweMessage.getContent().replace("\n", "<br/>");
		
		Context ctx = new Context();
		
		if (recipientSalutation != null) {
			ctx.setVariable("recipient", recipientSalutation);
		}
		ctx.setVariable("sender", cuName);
		ctx.setVariable("message", messageContent);
		ctx.setVariable("replyUrl", replyUrl);
		String html = templateEngine.process("email", ctx);

		mailService.sendMail(mime -> {
			MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
			mail.setSubject("Nachricht von " + cuName);
			mail.setFrom(adminMail, "GWE");
			for (GWEUser r : recipients) {
				mail.addTo(r.getEmail());
			}
			mail.setText(html, true);
		});

		if (gweMessage.getRecipientId() != -1) {
			return "redirect:/user/" + gweMessage.getRecipientId();
		} else if (gweMessage.getRecipientsYear() != -1) {
			return "redirect:/search?year=" + gweMessage.getRecipientsYear();
		} else {
			return "redirect:/search";
		}
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
