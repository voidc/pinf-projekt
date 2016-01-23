package de.gymwak.gwe.mvc;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	public String login() {
		if (authenticated()) {
			return "redirect:/user";
		}
		return "login";
	}

	private boolean authenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated();
	}
}
