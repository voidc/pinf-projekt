package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import de.gymnasiumwaldkraiburg.gwe.service.AsyncMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ActivationController {
    private GWERepository userRepository;
    private AsyncMailService mailService;

    @Autowired
    public ActivationController(GWERepository userRepository, AsyncMailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @RequestMapping(path = "/activate", method = RequestMethod.GET, params = {"token"})
    public String get(
            @RequestParam("token") String token, RedirectAttributes rAttr) {
        if (token == null) {
            rAttr.addFlashAttribute("status", "Aktivierung fehlgeschlagen");
            return "redirect:/login";
        }

        GWEUser activationUser = userRepository.findByActivationToken(token);
        if (activationUser != null) {
            activationUser.setActivated(true);
            userRepository.save(activationUser);
            rAttr.addFlashAttribute("status", "Aktivierung erfolgreich");
            return "redirect:/login";
        } else {
            rAttr.addFlashAttribute("status", "Aktivierung fehlgeschlagen");
            return "redirect:/login";
        }
    }

    @RequestMapping(path = "/activationmail", method = RequestMethod.GET)
    public String sendActivationMail(
            RedirectAttributes rAttr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());
        mailService.sendActivationMail(currentUser);
        rAttr.addFlashAttribute("status", "Aktivierungsmail gesendet");
        return "redirect:/user";
    }

}
