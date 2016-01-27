package de.gymwak.gwe.mvc;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.gymwak.gwe.model.GWEMessage;

@Controller
@RequestMapping("/impressum")
public class ImpressumController {

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "impressum";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String reportBug(@Valid GWEMessage message, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/impressum?error#feedback";
		}

		if (message.getMessage().isEmpty()) {
			return "redirect:/impressum?error=message#feedback";
		}

		// TODO Daten in eine (temporäre?) Datenbank(?) speichern

		return "redirect:/impressum?action=success#feedback";

	}

}
