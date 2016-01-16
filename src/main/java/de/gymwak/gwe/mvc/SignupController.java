package de.gymwak.gwe.mvc;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;

@Controller
@RequestMapping("/signup")
public class SignupController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;
	
	@Autowired
	public SignupController(GWERepository userRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.encoder = encoder;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String get() {
		return "signup";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String signup(@Valid GWEUser user, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
            return "signup";
        }
		user.setPassword(encoder.encode(user.getPassword()));
        user = userRepository.save(user);
        redirect.addFlashAttribute("globalMessage", "Successfully signed up");

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        UserDetails userDetails = new User(user.getEmail(),user.getPassword(), authorities);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/user";
		
	}

}
