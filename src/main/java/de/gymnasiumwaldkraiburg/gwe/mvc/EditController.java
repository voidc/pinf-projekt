package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser.GraduationType;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUserEdit;
import de.gymnasiumwaldkraiburg.gwe.service.AsyncMailService;
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
import java.util.Calendar;

@Controller
@RequestMapping("/edit")
public class EditController {
    private GWERepository userRepository;
    private PasswordEncoder encoder;
    private AsyncMailService mailService;

    @Autowired
    public EditController(GWERepository userRepository, PasswordEncoder encoder,
                          AsyncMailService mailService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mailService = mailService;
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

        if (((userEdit.getGraduationType() == GraduationType.ABITUR_WALDKRAIBURG || (
                userEdit.getGraduationType() == null
                        && currentUser.getGraduationType() == GraduationType.ABITUR_WALDKRAIBURG))
                && userEdit.getGraduationYear() < 2001) || userEdit.getGraduationYear() < 1940
                || userEdit.getGraduationYear() > Calendar.getInstance().get(Calendar.YEAR) + 2) {

            userEdit.setGraduationYear(currentUser.getGraduationYear());
            currentUser.applyUserEdit(userEdit);
            userRepository.save(currentUser);
            return "redirect:/edit?error=year";
        }

        currentUser.applyUserEdit(userEdit);

        if (changedUsername) {
            currentUser.setActivated(false);
            mailService.sendActivationMail(currentUser);
        }

        userRepository.save(currentUser);

        // #top stellt sicher, dass der Nutzer an den Anfang der Seite gelangt um die Meldung zu sehen
        return changedUsername ? "redirect:/logout" : "redirect:/edit?action=success#top";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"oldPassword", "password"})
    public String changePassword(
            @RequestParam String oldPassword, @RequestParam String password) {
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

    @ModelAttribute("graduationTypes")
    public GWEUser.GraduationType[] graduationTypes() {
        return GWEUser.GraduationType.values();
    }

}
