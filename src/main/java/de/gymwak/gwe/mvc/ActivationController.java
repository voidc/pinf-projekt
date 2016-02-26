package de.gymwak.gwe.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.MailGenerator;
import de.gymwak.gwe.service.TokenGenerator;

@Controller
@RequestMapping("/activation")
public class ActivationController {
	private GWERepository userRepository;
	private TokenGenerator tokenGen;
	private MailGenerator mailGen;

	@Autowired
	public ActivationController(GWERepository userRepository, TokenGenerator tokenGen, MailGenerator mailGen) {
		this.userRepository = userRepository;
		this.tokenGen = tokenGen;
		this.mailGen = mailGen;
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

		if (mailGen.sendActivationMail(currentUser, userRepository, tokenGen.nextToken()))
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
}
