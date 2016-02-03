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
		//@ModelAttribute übernimmt Werte aus dem Request (Form)
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());
		
		if (result.hasErrors()) {
			return "redirect:/edit?error";
		}

		boolean changedUsername = !userEdit.getEmail().equals(currentUser.getEmail());

		if (changedUsername && userRepository.findByEmail(userEdit.getEmail()) != null) {
			return "redirect:/edit?error";
		}

		currentUser.applyUserEdit(userEdit);

		userRepository.save(currentUser);

		return changedUsername ? "redirect:/logout" : "redirect:/user";
	}

}
