package de.gymwak.gwe.mvc;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

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
			@RequestParam(value = "y", required = false) String y,
			@RequestParam(value = "sort", required = false) String sort) {
		ModelAndView mav = new ModelAndView("search");
		Sort s = new Sort("lastName", "firstName", "graduationYear", "occupation", "id");

		if (sort != null) {
			switch (sort) {
			case "y":
				s = new Sort("graduationYear", "lastName", "firstName", "occupation", "id");
				mav.addObject("sort", "Abschlussjahr");
				break;
			case "o":
				s = new Sort("occupation", "lastName", "firstName", "graduationYear", "id");
				mav.addObject("sort", "Beschäftigung");
				break;
			case "n":
				s = new Sort("lastName", "firstName", "graduationYear", "occupation", "id");
				mav.addObject("sort", "Name");
				break;
			}
		}

		Stream<GWEUser> users = StreamSupport.stream(userRepository.findAll(s).spliterator(), false);

		if (y != null && y.length() > 0) {
			try {
				int graduationYear = Integer.parseInt(y);
				users = users.filter(u -> u.getGraduationYear() == graduationYear);
			} catch (NumberFormatException e) {
			}
		}

		if (q != null && q.length() > 0) {
			users = users.filter(u -> testQuery(u, q));
		}
		mav.addObject("results", users.collect(Collectors.toList()));
		return mav;
	}

	public boolean testQuery(GWEUser user, String query) {
		try {
			int year = Integer.parseInt(query);
			return user.getGraduationYear() == year;
		} catch (NumberFormatException e) {
			// Einfache Suche (nicht die schnellste und effektivste, aber besser als equalsIgnoreCase()
			return user.getFirstName().toLowerCase().contains(query.toLowerCase())
					|| user.getLastName().toLowerCase().contains(query.toLowerCase())
					|| user.getOccupation().toLowerCase().contains(query.toLowerCase());
		}
	}

}
