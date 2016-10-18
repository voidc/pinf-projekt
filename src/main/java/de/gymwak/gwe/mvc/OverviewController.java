package de.gymwak.gwe.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWEEventRepository;
import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

@Controller
public class OverviewController {
	private GWERepository userRepository;
	private GWEEventRepository eventRepository;

	@Autowired
	public OverviewController(GWERepository userRepository, GWEEventRepository eventRepository) {
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String get() {
		return "redirect:/overview";
	}

	@RequestMapping(path = "/overview", method = RequestMethod.GET)
	public ModelAndView overviewDetails() {
		ModelAndView mav = new ModelAndView("overview");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());
		Sort sort = new Sort("lastName", "firstName", "occupation", "disciplines", "id");

		mav.addObject("year",
				userRepository.findByGraduationYearAndGraduationTypeAndIdNot(
						currentUser.getGraduationYear(),
						currentUser.getGraduationType(),
						currentUser.getId(),
						sort)
		);

		mav.addObject("events", eventRepository.findAll());
		return mav;
	}
}
