package de.gymwak.gwe.mvc;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	@RequestMapping(method = RequestMethod.POST)
	public String search(@RequestParam("search") String searchTerm) {
		return "redirect:/search?q=" + searchTerm;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String browseSearch(Model model, @RequestParam(value = "q", required = false) String q, @RequestParam(value = "y", required = false) String y) {
		Stream<GWEUser> users = StreamSupport.stream(userRepository.findAll().spliterator(), false);

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
		
		model.addAttribute("results", users.collect(Collectors.toList()));
		return "search";
	}
	
	public boolean testQuery(GWEUser user, String query) {
		try {
			int year = Integer.parseInt(query);
			return user.getGraduationYear() == year;
		} catch(NumberFormatException e) {
			return user.getFirstName().equalsIgnoreCase(query)
					|| user.getLastName().equalsIgnoreCase(query)
					|| user.getOccupation().equalsIgnoreCase(query);
		}
	}

}
