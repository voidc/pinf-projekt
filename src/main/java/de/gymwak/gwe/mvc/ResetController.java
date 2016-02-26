package de.gymwak.gwe.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import de.gymwak.gwe.service.MailGenerator;
import de.gymwak.gwe.service.TokenGenerator;

@Controller
@RequestMapping("/reset")
public class ResetController {
	private GWERepository userRepository;
	private PasswordEncoder encoder;
	private TokenGenerator tokenGen;
	private MailGenerator mailGen;

	@Value("${gwe.reset-token-expiry}")
	private long resetTokenExpiry; //milliseconds

	@Autowired
	public ResetController(GWERepository userRepository, PasswordEncoder encoder,
			TokenGenerator tokenGen, MailGenerator mailGen) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.tokenGen = tokenGen;
		this.mailGen = mailGen;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(@RequestParam(value = "token", required = false) String token) {
		if (token == null)
			return "resetpasswordbymail";

		if (verifyToken(token))
			return "newpassword";
		else
			return "redirect:/reset?error&invalidToken=" + token;
	}

	@RequestMapping(method = RequestMethod.POST, params = { "password", "token" })
	public String resetPassword(@RequestParam String password, @RequestParam String token) {
		/*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		GWEUser currentUser = userRepository.findByEmail(auth.getName());

		if (currentUser != null)
			return "redirect:/reset";*/

		GWEUser resetUser = userRepository.findByResetToken(token);
		resetUser.setPassword(encoder.encode(password));
		userRepository.save(resetUser);

		// Log user in
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		UserDetails userDetails = new User(resetUser.getEmail(), resetUser.getPassword(), authorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, resetUser.getPassword(),
				authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);

		return "redirect:/user";
	}

	@RequestMapping(method = RequestMethod.POST, params = { "email" })
	public String sendResetMail(@RequestParam String email) {
		if (!mailGen.sendResetMail(userRepository.findByEmail(email), userRepository, tokenGen.nextToken(), resetTokenExpiry))
			return "redirect:/reset?error";
		else
			return "redirect:/reset?success";
	}

	private boolean verifyToken(String token) {
		// check if token exists
		GWEUser resetUser = userRepository.findByResetToken(token);
		if (resetUser == null)
			return false;

		// check if token is not expired (24h)
		long timeDiff = System.currentTimeMillis() - resetUser.getResetTokenIssued().getTime();
		if (timeDiff > resetTokenExpiry)
			return false;

		return true;
	}
}
