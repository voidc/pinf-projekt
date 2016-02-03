package de.gymwak.gwe.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

@Controller
public class UserController {
	private GWERepository userRepository;

	@Autowired
	public UserController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String get() {
		return "redirect:/user";
	}

	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public String userDetails() {
		return "user";
	}

	@RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
	public ModelAndView userProfile(@PathVariable int userId, @ModelAttribute("currentUser") GWEUser currentUser) {
		if(currentUser.getId() == userId)
			return new ModelAndView("redirect:/user");
		
		GWEUser user = userRepository.findOne((long) userId);
		if (user == null) {
			return new ModelAndView("redirect:/error?type=noSearchResults");
		}
		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		return mav;
	}

}
