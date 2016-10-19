package de.gymwak.gwe.mvc;

import de.gymwak.gwe.data.GWEEventRepository;
import de.gymwak.gwe.data.GWERepository;
import de.gymwak.gwe.model.GWEEvent;
import de.gymwak.gwe.model.GWEEventEdit;
import de.gymwak.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

@Controller
@EnableAsync
public class EventController {
    private GWERepository userRepository;
    private GWEEventRepository eventRepository;

    @Autowired
    public EventController(GWERepository userRepository, GWEEventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public String get() {
        return "redirect:/event/new";
    }

    @RequestMapping(value = "/event/new", method = RequestMethod.GET)
    public ModelAndView newEvent() {
        ModelAndView mav = new ModelAndView("newevent");
        Sort sort = new Sort("lastName", "firstName", "graduationYear", "graduationType", "occupation", "disciplines",
                "id");
        Iterable<GWEUser> allUsers = userRepository.findAll(sort);
        mav.addObject("users", allUsers);

        TreeSet<Integer> years = new TreeSet<Integer>();
        for (GWEUser user : allUsers) {
            years.add(user.getGraduationYear());
        }
        mav.addObject("years", years);
        return mav;
    }

    @RequestMapping(value = "/event/new", method = RequestMethod.POST)
    public String createEvent(@Valid GWEEvent event,
                              @RequestParam(value = "dateString", required = true) String date, RedirectAttributes rAttr) {
        Timestamp stamp = null;
        try {
            stamp = new Timestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(date).getTime());
        } catch (ParseException e) {
        }

        if (stamp.getTime() <= System.currentTimeMillis()) {
            rAttr.addFlashAttribute("event", event);
            rAttr.addFlashAttribute("date", date);
            return "redirect:/event/new?error=timePast";
        }

        event.setTime(stamp);
        if (event.hasParticipant(event.getOrganizer())) {
            event.getParticipants().remove(event.getOrganizer());
        }

        eventRepository.save(event);
        return "redirect:/event/" + event.getId();
    }

    @RequestMapping(value = "/event/{eventId}/delete", method = RequestMethod.POST)
    public String deleteEvent(
            @PathVariable long eventId) {
        GWEEvent event = (GWEEvent) eventRepository.findOne(eventId);
        if (event == null) {
            return "redirect:/error?type=noSearchResults";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());
        if (!currentUser.isActivated() || event.getOrganizer() != currentUser) {
            return "redirect:/event/" + eventId + "?error";
        }

        eventRepository.delete(event);
        return "redirect:/overview";
    }

    @RequestMapping(value = "/event/{eventId}", method = RequestMethod.GET)
    public ModelAndView event(
            @PathVariable long eventId) {
        GWEEvent event = (GWEEvent) eventRepository.findOne(eventId);
        if (event == null) {
            return new ModelAndView("redirect:/error?type=noSearchResults");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        ModelAndView mav = new ModelAndView("event");
        mav.addObject("event", event);
        mav.addObject("dateString",
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(event.getTime().getTime())));
        mav.addObject("participant", event.hasParticipant(currentUser) || event.getOrganizer().equals(currentUser));

        String participants = "";
        Iterator<GWEUser> i = event.getParticipants().iterator();
        while (i.hasNext()) {
            GWEUser user = i.next();
            participants = participants + user.getFirstName() + " " + user.getLastName() + (i.hasNext() ? ", " : "");
        }
        mav.addObject("participants", participants);
        return mav;
    }

    @RequestMapping(value = "/event/{eventId}/edit", method = RequestMethod.GET)
    public ModelAndView getEdit(
            @PathVariable int eventId) {
        GWEEvent event = (GWEEvent) eventRepository.findOne((long) eventId);
        if (event == null) {
            return new ModelAndView("redirect:/error?type=noSearchResults");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!event.getOrganizer().equals(currentUser)) {
            return new ModelAndView("redirect:/event/" + event.getId());
        }

        ModelAndView mav = new ModelAndView("editevent");

        Sort sort = new Sort("lastName", "firstName", "graduationYear", "graduationType", "occupation", "disciplines",
                "id");
        Iterable<GWEUser> allUsers = userRepository.findAll(sort);
        mav.addObject("allUsers", allUsers);

        TreeSet<Integer> years = new TreeSet<Integer>();
        for (GWEUser user : allUsers) {
            years.add(user.getGraduationYear());
        }
        mav.addObject("years", years);

        mav.addObject("event", event);
        mav.addObject("dateString",
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(event.getTime().getTime())));
        return mav;
    }

    @RequestMapping(value = "/event/{eventId}/edit", method = RequestMethod.POST)
    public ModelAndView applyEdit(
            @Valid GWEEventEdit edit, @RequestParam(value = "dateString", required = true) String date,
            @PathVariable int eventId) {
        Timestamp stamp = null;
        try {
            stamp = new Timestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(date).getTime());
        } catch (ParseException e) {
        }

        GWEEvent event = (GWEEvent) eventRepository.findOne((long) eventId);
        if (event == null) {
            return new ModelAndView("redirect:/error?type=noSearchResults");
        }

        if (stamp.getTime() <= System.currentTimeMillis()) {
            ModelAndView mav = new ModelAndView("redirect:/event/" + eventId + "/edit?error=timePast");
            mav.addObject("event", event);
            mav.addObject("newDate", date);
            return mav;
        }

        edit.setTime(stamp);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!event.getOrganizer().equals(currentUser)) {
            return new ModelAndView("redirect:/event/" + event.getId());
        }
        if (edit.hasParticipant(edit.getOrganizer())) {
            edit.getParticipants().remove(edit.getOrganizer());
        }

        event.applyEventEdit(edit);
        eventRepository.save(event);
        return new ModelAndView("redirect:/event/" + event.getId());
    }
}
