package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWEEventRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEEvent;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
public class OverviewController {
    private GWERepository userRepository;
    private GWEEventRepository eventRepository;

    @Autowired
    public OverviewController(GWERepository userRepository, GWEEventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String get() {
        return "redirect:/overview";
    }

    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public ModelAndView overviewDetails() {
        ModelAndView mav = new ModelAndView("overview");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());
        Sort sort = new Sort("lastName", "firstName", "occupation", "disciplines", "id");

        mav.addObject("year", userRepository
                .findByGraduationYearAndGraduationTypeAndIdNot(currentUser.getGraduationYear(),
                        currentUser.getGraduationType(), currentUser.getId(), sort));

        Stream<GWEEvent> events = StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .filter(e -> e.hasParticipant(currentUser) || e.getOrganizer().equals(currentUser));

        mav.addObject("events", (Iterable<GWEEvent>) events::iterator);
        mav.addObject("admin", currentUser.isAdmin());
        return mav;
    }
}
