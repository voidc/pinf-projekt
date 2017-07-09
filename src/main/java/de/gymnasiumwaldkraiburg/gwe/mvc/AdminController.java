package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWEBannerRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEBanner;
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

@Controller
@RequestMapping("/admin")
public class AdminController {
    private GWERepository userRepository;
    private GWEBannerRepository bannerRepository;

    @Autowired
    public AdminController(GWERepository userRepository, GWEBannerRepository bannerRepository) {
        this.userRepository = userRepository;
        this.bannerRepository = bannerRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() throws NoHandlerFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (currentUser.isAdmin()) {
            ModelAndView mav = new ModelAndView("admin");

            if (bannerRepository.count() > 0) {
                GWEBanner banner = bannerRepository.findAll().iterator().next();

                mav.addObject("content", banner.getContent().replace("\n", "<br />"));
                mav.addObject("color", banner.getColor().desc);
                mav.addObject("dismissible", banner.isDismissible());
            }

            return mav;
        } else {
            return new ModelAndView("redirect:/error");
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = {"update"})
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

    @RequestMapping(method = RequestMethod.POST, params = {"delete"})
    public String deleteBanner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!currentUser.isAdmin()) {
            return "redirect:/overview";
        }

        bannerRepository.deleteAll();

        return "redirect:/admin#banner";
    }

    @RequestMapping(value = "/bannerClose", method = RequestMethod.GET)
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
