package de.gymnasiumwaldkraiburg.gwe.mvc;


import de.gymnasiumwaldkraiburg.gwe.data.GWEBannerRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEBanner;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class BannerAdvice {
    private GWERepository userRepository;
    private GWEBannerRepository bannerRepository;

    @Autowired
    public BannerAdvice(GWERepository userRepository, GWEBannerRepository bannerRepository) {
        this.userRepository = userRepository;
        this.bannerRepository = bannerRepository;
    }

    @ModelAttribute("banner")
    public GWEBanner banner() {
        if (bannerRepository.count() > 0) {
            GWEBanner banner = bannerRepository.findAll().iterator().next();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
                return null;
            }

            GWEUser currentUser = userRepository.findByEmail(auth.getName());

            if (currentUser == null || (banner.isDismissible() && currentUser.isBannerDismissed())) {
                return null;
            }

            return banner;
        }
        return null;
    }
}
