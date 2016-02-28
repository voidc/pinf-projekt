package de.gymwak.gwe.mvc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWEEventRepository;
import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEEvent;
import de.gymwak.gwe.model.GWEUser;

@Controller
@EnableAsync
public class EventController {
	private GWERepository userRepository;
	private GWEEventRepository eventRepository;

	@Autowired
	public EventController(GWERepository userRepository, GWEEventRepository eventRepository) {
		this.userRepository = userRepository;
		this.eventRepository = eventRepository;
	}

	@RequestMapping(value = "/event", method = RequestMethod.GET)
	public String get() {
		return "redirect:/event/new";
	}

	@RequestMapping(value = "/event/new", method = RequestMethod.GET)
	public ModelAndView newEvent() {
		ModelAndView mav = new ModelAndView("newevent");
		Sort sort = new Sort("lastName", "firstName", "graduationYear", "graduationType", "occupation", "discipline", "id");
		mav.addObject("users", userRepository.findAll(sort));
		return mav;
	}

	@RequestMapping(path = "/event/delete", method = RequestMethod.POST)
	public String deleteEvent(@RequestParam long eventId) {
		GWEEvent event = eventRepository.findOne((long) eventId);
		if (event == null) {
			return "redirect:/error?type=noSearchResults";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());
		if (!currentUser.isActivated() || event.getOrganizerId() != currentUser.getId()) {
			return "redirect:/event/" + eventId + "?error";
		}

		eventRepository.delete(event);
		return "redirect:/logout";
	}

	@RequestMapping(value = "/event/{eventId}", method = RequestMethod.GET)
	public ModelAndView event(@PathVariable int eventId) {
		GWEEvent event = eventRepository.findOne((long) eventId);
		if (event == null) {
			return new ModelAndView("redirect:/error?type=noSearchResults");
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		ModelAndView mav = new ModelAndView("event");
		mav.addObject("event", event);
		mav.addObject("organizer", userRepository.findOne(event.getOrganizerId()));
		mav.addObject("participant", event.hasParticipant(currentUser.getId()) || event.getOrganizerId() == currentUser.getId());

		String participants = "";
		for (int i = event.getParticipants().length - 1; i >= 0; i--) {
			GWEUser user = userRepository.findOne(event.getParticipants()[i]);
			participants = participants + user.getFirstName() + " " + user.getLastName() + (i > 0 ? ", " : "");
		}
		mav.addObject("participants", participants);
		return mav;
	}

	@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
	public String createEvent(@Valid GWEEvent event) {
		eventRepository.save(event);
		return "redirect:/event/" + event.getId();
	}
}
