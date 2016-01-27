package de.gymwak.gwe.mvc;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.model.GWEUserEdit;

@Controller
@RequestMapping("/edit")
public class EditController {
	private GWERepository userRepository;

	@Autowired
	public EditController(GWERepository userRepository) {
		this.userRepository = userRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "edit";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String edit(@Valid GWEUserEdit userEdit, BindingResult result) {
		if (result.hasErrors()) {
			return "redirect:/edit?error";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "redirect:/login?error";
		}

		GWEUser gweUser = userRepository.findByEmail(auth.getName());
		boolean changedUsername = !userEdit.getEmail().equals(gweUser.getEmail());

		if (changedUsername && userRepository.findByEmail(userEdit.getEmail()) != null) {
			return "redirect:/edit?error";
		}

		gweUser.applyUserEdit(userEdit);

		userRepository.save(gweUser);

		return changedUsername ? "redirect:/logout" : "redirect:/user";
	}

}
