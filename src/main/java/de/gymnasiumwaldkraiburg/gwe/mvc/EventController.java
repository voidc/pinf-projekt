package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWEEventRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEEvent;
import de.gymnasiumwaldkraiburg.gwe.model.GWEEventEdit;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
        List<Integer> years = userRepository.findDistinctGraduationYears();
        mav.addObject("years", years);
        return mav;
    }

    @RequestMapping(value = "/api/year/{year}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<GWEUser> apiGetYear(@PathVariable int year) {
        return userRepository.findByGraduationYear(year);
    }

    @RequestMapping(value = "/event/new", method = RequestMethod.POST)
    public String createEvent(@Valid GWEEvent event,
                              @RequestParam(value = "dateString", required = true) String date,
                              RedirectAttributes rAttr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!currentUser.isActivated() || event.getOrganizer() != currentUser) {
            rAttr.addFlashAttribute("event", event);
            rAttr.addFlashAttribute("date", date);
            return "redirect:/event/new?error";
        }

        Timestamp stamp = null;
        try {
            stamp = new Timestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(date).getTime());
        } catch (ParseException e) {
        }

        if (stamp == null || stamp.getTime() <= System.currentTimeMillis()) {
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
    public String deleteEvent(@PathVariable long eventId) {
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
    public ModelAndView event(@PathVariable long eventId) {
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

        StringBuilder participants = new StringBuilder();
        Iterator<GWEUser> i = event.getParticipants().iterator();
        while (i.hasNext()) {
            GWEUser user = i.next();
            participants.append(user.getFullName()).append(i.hasNext() ? ", " : "");
        }
        mav.addObject("participants", participants.toString());
        return mav;
    }

    @RequestMapping(value = "/event/{eventId}/edit", method = RequestMethod.GET)
    public ModelAndView getEdit(@PathVariable int eventId) {
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

        List<Integer> years = userRepository.findDistinctGraduationYears();
        mav.addObject("years", years);

        mav.addObject("event", event);
        Date date = new Date(event.getTime().getTime());
        mav.addObject("dateString", new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date));
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

        if (stamp == null || stamp.getTime() <= System.currentTimeMillis()) {
            ModelAndView mav = new ModelAndView("redirect:/event/" + eventId + "/edit?error=timePast");
            mav.addObject("event", event);
            mav.addObject("newDate", date);
            return mav;
        }

        edit.setTime(stamp);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!event.getOrganizer().equals(currentUser) || !currentUser.isActivated()) {
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
