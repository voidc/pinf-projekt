package de.gymwak.gwe.mvc;

import java.util.Calendar;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.model.GWEUserEdit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping("/edit")
public class EditController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;

	@Autowired
	public EditController(GWERepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
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
		
		if (currentUser.getGraduationYear() < 2001
				|| currentUser.getGraduationYear() > Calendar.getInstance().get(Calendar.YEAR) + 2) {
			userEdit.setGraduationYear(currentUser.getGraduationYear());
			currentUser.applyUserEdit(userEdit);
			userRepository.save(currentUser);
			return "redirect:/edit?error=year";
		}

		currentUser.applyUserEdit(userEdit);

		userRepository.save(currentUser);

		// #top stellt sicher, dass der Nutzer an den Anfang der Seite gelangt um die Meldung zu sehen
		return changedUsername ? "redirect:/logout" : "redirect:/edit?action=success#top";
	}

	@RequestMapping(method = RequestMethod.POST, params = { "oldPassword", "password" })
	public String changePassword(@RequestParam String oldPassword, @RequestParam String password) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (!encoder.matches(oldPassword, currentUser.getPassword())) {
			return "redirect:/edit?error=password#change-pw";
		}

		currentUser.setPassword(encoder.encode(password));
		userRepository.save(currentUser);

		// #top stellt sicher, dass der Nutzer an den Anfang der Seite gelangt um die Meldung zu sehen
		return "redirect:/edit?action=success#top";
	}

	@ModelAttribute("disciplines")
	public GWEUser.Discipline[] disciplines() {
		return GWEUser.Discipline.values();
	}

}
