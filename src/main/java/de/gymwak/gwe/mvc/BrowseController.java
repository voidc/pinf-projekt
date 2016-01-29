package de.gymwak.gwe.mvc;

import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;

@Controller
public class BrowseController {
	private GWERepository userRepository;

	@Autowired
	public BrowseController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(path = "/browse", method = RequestMethod.GET)
	public String browse(Model model) {
		model.addAttribute("browse", userRepository.findAll());
		return "browse";
	}
	
	@RequestMapping(path = "/browse/search", method = RequestMethod.GET)
	public String browseSearch(Model model, @RequestParam("text") String searchText) {
		model.addAttribute("browse", userRepository.findByEmail(searchText));
		return "browse";
	}
}
