package de.gymwak.gwe.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/info")
public class InfoController {
	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "info";
	}
}
