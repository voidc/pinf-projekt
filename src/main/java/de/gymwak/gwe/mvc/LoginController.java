package de.gymwak.gwe.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.gymwak.gwe.model.GWEUser;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	public String login(@ModelAttribute("currentUser") GWEUser currentUser) {
		if (currentUser != null) {
			return "redirect:/";
		}
		return "login";
	}
}
