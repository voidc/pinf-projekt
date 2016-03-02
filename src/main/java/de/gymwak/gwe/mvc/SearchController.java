package de.gymwak.gwe.mvc;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/search")
public class SearchController {
	private GWERepository userRepository;

	@Autowired
	public SearchController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView search(@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "disc", required = false) String disc,
			@RequestParam(value = "sort", required = false) String sort) {
		ModelAndView mav = new ModelAndView("search");
		Sort s = new Sort("lastName", "firstName", "graduationYear", "graduationType", "occupation", "discipline", "id");

		if (sort != null) {
			switch (sort) {
			case "year":
				s = new Sort("graduationYear", "graduationType", "lastName", "firstName", "occupation", "discipline", "id");
				mav.addObject("sortText", "Abschlussjahr");
				break;
			case "occu":
				s = new Sort("occupation", "lastName", "firstName", "graduationYear", "graduationType", "discipline", "id");
				mav.addObject("sortText", "Beschäftigung");
				break;
			case "name":
				s = new Sort("lastName", "firstName", "graduationYear", "graduationType", "occupation", "discipline", "id");
				mav.addObject("sortText", "Name");
				break;
			case "disc":
				s = new Sort("discipline", "lastName", "firstName", "graduationYear", "graduationType", "occupation", "id");
				mav.addObject("sortText", "Fachgebiet");
				break;
			}
		}

		Stream<GWEUser> users = StreamSupport.stream(userRepository.findAll(s).spliterator(), false);

		if(disc != null && !disc.isEmpty()) {
			try {
				GWEUser.Discipline discipline = GWEUser.Discipline.valueOf(disc);
				users = users.filter(u -> u.getDiscipline().equals(discipline));
				mav.addObject("disc", discipline);
			} catch(IllegalArgumentException e) {
			}
		}
		
		if (q != null && q.length() > 0) {
			try {
				int graduationYear = Integer.parseInt(q);
				users = users.filter(u -> u.getGraduationYear() == graduationYear);
				mav.addObject("year", graduationYear);
			} catch (NumberFormatException e) {
				users = users.filter(u -> testQuery(u, q));
				mav.addObject("query", q);
			}
		}
		
		if (!mav.getModel().containsKey("year") && year != null && year.length() > 0) {	
			try {
				int graduationYear = Integer.parseInt(year);
				users = users.filter(u -> u.getGraduationYear() == graduationYear);
				mav.addObject("year", graduationYear);
			} catch (NumberFormatException e) {
			}
		}

		mav.addObject("results", users.collect(Collectors.toList()));
		return mav;
	}

	@ModelAttribute("disciplines")
	public GWEUser.Discipline[] disciplines() {
		return GWEUser.Discipline.values();
	}

	private boolean testQuery(GWEUser user, String query) {
		// Einfache Suche (nicht die schnellste und effektivste, aber besser als equalsIgnoreCase()
		return user.getFirstName().toLowerCase().contains(query.toLowerCase())
				|| user.getLastName().toLowerCase().contains(query.toLowerCase())
				|| user.getOccupation().toLowerCase().contains(query.toLowerCase())
				|| user.getDiscipline().desc.toLowerCase().contains(query.toLowerCase());
	}

}
