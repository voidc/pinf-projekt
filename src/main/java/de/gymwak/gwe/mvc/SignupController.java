package de.gymwak.gwe.mvc;

import java.util.Calendar;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.model.GWEUser.GraduationType;
import de.gymwak.gwe.service.MailGenerator;
import de.gymwak.gwe.service.TokenGenerator;

@Controller
@RequestMapping("/signup")
public class SignupController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;
	private TokenGenerator tokenGen;
	private MailGenerator mailGen;

	@Autowired
	public SignupController(GWERepository userRepository, PasswordEncoder encoder,
			TokenGenerator tokenGen, MailGenerator mailGen) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.tokenGen = tokenGen;
		this.mailGen = mailGen;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "signup";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String signup(@Valid GWEUser user, BindingResult result, RedirectAttributes rAttr) {
		if (result.hasErrors()) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error";
		}

		if (userRepository.findByEmail(user.getEmail()) != null) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error=email";
		}

		if ((user.getGraduationType() == GraduationType.ABITUR_WALDKRAIBURG && user.getGraduationYear() < 2001)
				|| user.getGraduationYear() < 1940
				|| user.getGraduationYear() > Calendar.getInstance().get(Calendar.YEAR) + 2) {
			rAttr.addFlashAttribute("signupUser", user);
			return "redirect:/signup?error=year";
		}

		user.setPassword(encoder.encode(user.getPassword()));
		user.setActivated(false);
		user = userRepository.save(user);

		if (!mailGen.sendActivationMail(user, userRepository, tokenGen.nextToken()))
			return "redirect:/signup?error";

		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		UserDetails userDetails = new User(user.getEmail(), user.getPassword(), authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:/user";

	}

	@ModelAttribute("disciplines")
	public GWEUser.Discipline[] disciplines() {
		return GWEUser.Discipline.values();
	}

	@ModelAttribute("graduationTypes")
	public GWEUser.GraduationType[] graduationTypes() {
		return GWEUser.GraduationType.values();
	}
}
