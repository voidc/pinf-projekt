package de.gymwak.gwe.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

@Controller
@RequestMapping({"/user", "/"})
public class UserController {
	private GWERepository userRepository;
	
	@Autowired
	public UserController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView userDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null || !auth.isAuthenticated()) {
			return new ModelAndView("redirect:/login?error");
		}
		
		Assert.notNull(auth.getName(), "username");
		Assert.notNull(userRepository, "repo");
		
		GWEUser gweUser = userRepository.findByEmail(auth.getName());
		ModelAndView mav = new ModelAndView("user");
		mav.addObject("currentUser", gweUser);
		return mav;
	}

}
