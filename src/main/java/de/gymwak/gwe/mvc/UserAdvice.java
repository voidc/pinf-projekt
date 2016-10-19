package de.gymwak.gwe.mvc;

import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserAdvice {
    private GWERepository userRepository;

    @Autowired
    public UserAdvice(GWERepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("currentUser")
    public GWEUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return null;
        }

        return userRepository.findByEmail(auth.getName());
    }

}
