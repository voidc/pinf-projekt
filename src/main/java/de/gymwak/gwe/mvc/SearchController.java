package de.gymwak.gwe.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/search")
public class SearchController {

	@RequestMapping(method = RequestMethod.POST)
	public String search(@RequestParam("search") String searchTerm) {
		return "redirect:/browse?searchtext=" + searchTerm;
	}

}
