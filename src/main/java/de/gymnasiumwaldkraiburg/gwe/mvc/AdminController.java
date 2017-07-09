package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWEBannerRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWEEventRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEBanner;
import de.gymnasiumwaldkraiburg.gwe.model.GWEEvent;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private GWERepository userRepository;
    private GWEBannerRepository bannerRepository;
    private GWEEventRepository eventRepository;

    @Autowired
    public AdminController(GWERepository userRepository, GWEBannerRepository bannerRepository, GWEEventRepository eventRepository) {
        this.userRepository = userRepository;
        this.bannerRepository = bannerRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() throws NoHandlerFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (currentUser.isAdmin()) {
            ModelAndView mav = new ModelAndView("admin");

            if (bannerRepository.count() > 0) {
                GWEBanner banner = bannerRepository.findAll().iterator().next();

                mav.addObject("sampleBanner", banner);
            }

            return mav;
        } else {
            return new ModelAndView("redirect:/error");
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = {"updateBanner"})
    public String updateBanner(@RequestParam(value = "content", required = true) String content,
                               @RequestParam String color, @RequestParam(required = false) boolean dismissible) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!currentUser.isAdmin()) {
            return "redirect:/overview";
        }

        GWEBanner banner = new GWEBanner();
        banner.setContent(content);
        banner.setColor(GWEBanner.Color.valueOf(color));
        banner.setDismissible(dismissible);

        bannerRepository.deleteAll();
        bannerRepository.save(banner);

        Iterable<GWEUser> users = userRepository.findAll();
        users.forEach(u -> u.setBannerDismissed(false));
        userRepository.save(users);

        return "redirect:/admin#banner";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"deleteBanner"})
    public String deleteBanner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!currentUser.isAdmin()) {
            return "redirect:/overview";
        }

        bannerRepository.deleteAll();

        return "redirect:/admin#banner";
    }

    @RequestMapping(method = RequestMethod.POST, params = {"deleteUser"})
    public String deleteUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam int gradYear) {
        System.out.println("success?");

        List<GWEUser> users = userRepository.findByGraduationYear(gradYear);
        GWEUser deleteUser = null;

        for (GWEUser user : users) {
            // TODO multiple users of same name/gradYear seperation

            if (!user.isAdmin() && user.getFirstName().equals(firstName) && user.getLastName().equals(lastName)) {
                deleteUser = user;
            }
        }

        if (deleteUser == null) {
            return "redirect:/admin?delete=error#deleteUser";
        } else {
            eventRepository.delete(eventRepository.findByOrganizerId(deleteUser.getId()));

            Iterable<GWEEvent> events = eventRepository.findAll();
            for (GWEEvent e : events) {
                if (e.hasParticipant(deleteUser)) {
                    List<GWEUser> p = e.getParticipants();
                    p.remove(deleteUser);
                    e.setParticipants(p);
                    eventRepository.save(e);
                }
            }

            userRepository.delete(deleteUser);

            return "redirect:/admin?delete=success#deleteUser";
        }
    }

    @RequestMapping(value = "/closeBanner", method = RequestMethod.GET)
    public String bannerClose() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (bannerRepository.count() > 0) {
            GWEBanner banner = bannerRepository.findAll().iterator().next();

            if (banner.isDismissible()) {
                currentUser.setBannerDismissed(true);
                userRepository.save(currentUser);
            }
        }

        return "redirect:/error";
    }

    @ModelAttribute("colors")
    public GWEBanner.Color[] colors() {
        return GWEBanner.Color.values();
    }
}
