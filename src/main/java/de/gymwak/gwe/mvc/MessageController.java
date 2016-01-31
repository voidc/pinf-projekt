package de.gymwak.gwe.mvc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEMessage;
import de.gymwak.gwe.model.GWEUser;

@Controller
public class MessageController {
	private GWERepository userRepository;
	private JavaMailSender mailSender;

	@Autowired
	public MessageController(GWERepository userRepository, JavaMailSender mailSender) {
		this.userRepository = userRepository;
		this.mailSender = mailSender;
	}
	
	@RequestMapping(value = "/message", method = RequestMethod.GET)
	public ModelAndView get(@RequestParam String to) {
		ModelAndView mav = new ModelAndView("message");
		long recipientId = Long.parseLong(to);
		GWEUser recipient = userRepository.findOne(recipientId);
		mav.addObject("recipient", recipient);
		return mav;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendMessage(@Valid GWEMessage gweMessage) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());
		GWEUser recipientUser = userRepository.findOne(gweMessage.getRecipientId());

		String cuName = currentUser.getFirstName() + " " + currentUser.getLastName();
		String ruName = recipientUser.getFirstName() + " " + recipientUser.getLastName();

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setSubject("Nachricht von " + cuName);
		mail.setFrom("gwe@gymnasiumwaldkraiburg.de");
		mail.setReplyTo(currentUser.getEmail());
		mail.setTo(recipientUser.getEmail());
		mail.setText(String.format("Hallo %s,\n"
				+ "%s hat dir über das Ehemaligen Portal des Gymnsiums Waldkraiburg eine Nachricht gesendet:\n" + "%s",
				ruName, cuName, gweMessage.getContent()));

		mailSender.send(mail);
		return "redirect:/user/" + recipientUser.getId();
	}

}
