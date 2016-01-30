package de.gymwak.gwe.mvc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

@Controller
public class BrowseController {
	private GWERepository userRepository;

	@Autowired
	public BrowseController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	/*@RequestMapping(path = "/browse", method = RequestMethod.GET)
	public String browse(Model model) {
		model.addAttribute("browse", userRepository.findAll());
		return "browse";
	}*/

	@RequestMapping(path = "/browse", method = RequestMethod.GET)
	public String browseSearch(Model model, @RequestParam(value = "searchtext", required = false) String searchText,
			@RequestParam(value = "gradYear", required = false) String gradYear) {
		List<GWEUser> users = (List<GWEUser>) userRepository.findAll();

		if (gradYear != null && gradYear.length() > 0) {
			try {
				int graduationYear = Integer.parseInt(gradYear);
				List<GWEUser> foundUsers = (List<GWEUser>) userRepository.findByGraduationYear(graduationYear);
				users = (List<GWEUser>) substractFromList(users, foundUsers);
			} catch (NumberFormatException e) {
			}
		}

		// Abbruchfall
		if (users.isEmpty()) {
			model.addAttribute("browse", users);
			return "browse";
		}

		// Search-Test am Ende - dauert am längsten wenn users viele Einträge enthält
		if (searchText != null && searchText.length() > 0) {
			List<GWEUser> foundUsers = (List<GWEUser>) userRepository.findByEmailContainingIgnoreCase(searchText);
			foundUsers.addAll((Collection<? extends GWEUser>) userRepository.findByFirstNameContainingIgnoreCase(searchText));
			foundUsers.addAll((Collection<? extends GWEUser>) userRepository.findByLastNameContainingIgnoreCase(searchText));
			try {
				int graduationYear = Integer.parseInt(searchText);
				foundUsers.addAll((Collection<? extends GWEUser>) userRepository.findByGraduationYearContaining(graduationYear));
			} catch (NumberFormatException e) {
			}
			foundUsers.addAll((Collection<? extends GWEUser>) userRepository.findByOccupationContainingIgnoreCase(searchText));
			users = (List<GWEUser>) substractFromList(users, foundUsers);
		}
		model.addAttribute("browse", users);
		return "browse";
	}
	
	public static List<GWEUser> substractFromList(List<GWEUser> collection, Iterable<GWEUser> substraction) {
		List<GWEUser> result = new LinkedList<GWEUser>();
		List<GWEUser> substractionCol = (List<GWEUser>) substraction;
		for (GWEUser object : collection) {
			if (substractionCol.contains(object)) {
				result.add(object);
			}
		}
		return result;
	}
}
