package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
    private GWERepository userRepository;

    @Autowired
    public UserController(GWERepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public String userDetails() {
        return "user";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public String deleteUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());
        userRepository.delete(currentUser);
        return "redirect:/logout";
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public ModelAndView userProfile(
            @PathVariable int userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (currentUser.getId() == userId)
            return new ModelAndView("redirect:/");

        GWEUser user = userRepository.findOne((long) userId);
        if (user == null) {
            return new ModelAndView("redirect:/error?type=noSearchResults");
        }
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("user", user);
        return mav;
    }

}
