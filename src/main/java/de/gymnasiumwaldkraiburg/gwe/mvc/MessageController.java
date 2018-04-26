package de.gymnasiumwaldkraiburg.gwe.mvc;

import de.gymnasiumwaldkraiburg.gwe.data.GWEEventRepository;
import de.gymnasiumwaldkraiburg.gwe.data.GWERepository;
import de.gymnasiumwaldkraiburg.gwe.model.GWEEvent;
import de.gymnasiumwaldkraiburg.gwe.model.GWEMessage;
import de.gymnasiumwaldkraiburg.gwe.model.GWEUser;
import de.gymnasiumwaldkraiburg.gwe.service.AsyncMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@EnableAsync
public class MessageController {
    private GWERepository userRepository;
    private GWEEventRepository eventRepository;
    private AsyncMailService mailService;
    private TemplateEngine templateEngine;

    @Value("${server.port}")
    private String serverPort;

    @Value("${EMAIL_USER}")
    private String adminMail;

    @Value("${GWE_ADDRESS}")
    private String gweAddress;

    @Autowired
    public MessageController(GWERepository userRepository, GWEEventRepository eventRepository,
                             AsyncMailService mailService, TemplateEngine templateEngine) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public ModelAndView get(@RequestParam String to) {
        ModelAndView mav = new ModelAndView("message");
        if (to.startsWith("year")) {
            mav.addObject("year", to.substring(4));
        } else if (to.startsWith("event")) {
            try{ 
            long recipientId = Long.parseLong(to); 
            GWEUser recipient = userRepository.findOne(recipientId); 
            mav.addObject("recipient", recipient); 
            } 
            catch (NumberFormatException nfe) { 
                System.out.println("NumberFormatException: " + nfe.getMessage()); 
            }             
        } else {
            long recipientId = Long.parseLong(to);
            GWEUser recipient = userRepository.findOne(recipientId);
            mav.addObject("recipient", recipient);
        }
        return mav;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String sendMessage(HttpServletRequest request,
                                  @Valid GWEMessage gweMessage) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        GWEUser currentUser = userRepository.findByEmail(auth.getName());

        if (!currentUser.isActivated()) {
            if (gweMessage.getRecipientId() != -1) {
                return "redirect:/user/" + gweMessage.getRecipientId();
            } else if (gweMessage.getRecipientsYear() != -1) {
                return "redirect:/search?year=" + gweMessage.getRecipientsYear();
            } else if (gweMessage.getEventId() != -1) {
                return "redirect:/event/" + gweMessage.getEventId();
            } else {
                return "redirect:/search";
            }
        }

        List<GWEUser> recipients;
        GWEEvent event = null;
        String recipientSalutation;
        boolean recipientYear = false;
        boolean usermail = true;

        String cuName = currentUser.getFirstName() + " " + currentUser.getLastName();
        String subject = "Nachricht von " + cuName;

        if (gweMessage.getRecipientId() != -1) {
            GWEUser recipientUser = userRepository.findOne(gweMessage.getRecipientId());
            recipients = Collections.singletonList(recipientUser);
            recipientSalutation = recipientUser.getFirstName() + " " + recipientUser.getLastName();
        } else if (gweMessage.getRecipientsYear() != -1) {
            recipients = userRepository.findByGraduationYear(gweMessage.getRecipientsYear());
            recipientSalutation = recipientUser.getFirstName();
            recipientYear = true;
        } else if (gweMessage.getEventId() != -1) {
            event = (GWEEvent) eventRepository.findOne(gweMessage.getEventId());
            recipients = event.getParticipants();
            recipientSalutation = null;
        } else {
            recipients = new ArrayList<>();
            userRepository.findAll().forEach(recipients::add);
            recipientSalutation = null;
            usermail = false;

            if (gweMessage.getSubject() != null && gweMessage.getSubject().length() > 0) {
                subject = gweMessage.getSubject();
            }
        }

        String replyUrl = String.format(gweAddress + "message?to=%d", currentUser.getId());
        String imgUrl = String.format(gweAddress + "img/logo-notext-transparent-sq-46px.png");
        String settingsUrl = String.format(gweAddress + "edit");
        String feedbackUrl = String.format(gweAddress + "impressum#feedback");
        String messageContent = gweMessage.getContent().replace("\n", "<br/>");
        String finalSubject = subject;

        Context ctx = new Context();

        if (recipientSalutation != null) {
            ctx.setVariable("recipient", recipientSalutation);
            ctx.setVariable("event", event);
        }
        ctx.setVariable("usermail", usermail);
        ctx.setVariable("recipientYear", recipientYear);
        ctx.setVariable("sender", cuName);
        ctx.setVariable("message", messageContent);
        ctx.setVariable("replyUrl", replyUrl);
        ctx.setVariable("imgUrl", imgUrl);
        ctx.setVariable("settingsUrl", settingsUrl);
        ctx.setVariable("feedbackUrl", feedbackUrl);
        String html = templateEngine.process("email", ctx);

        mailService.sendMail(mime -> {
            MimeMessageHelper mail = new MimeMessageHelper(mime, true, "UTF-8");
            mail.setSubject(finalSubject);
            mail.setFrom(adminMail, "GWE");
            if (recipients != null) {
                for (GWEUser r : recipients) {
                    mail.addTo(r.getEmail());
                }
            }
            mail.setText(html, true);
        });

        if (gweMessage.getRecipientId() != -1) {
            return "redirect:/user/" + gweMessage.getRecipientId();
        } else if (gweMessage.getRecipientsYear() != -1) {
            return "redirect:/search?year=" + gweMessage.getRecipientsYear();
        } else if (gweMessage.getEventId() != -1) {
            return "redirect:/event/" + gweMessage.getEventId();
        } else if (usermail == false) {
            return "redirect:" + request.getHeader("Referer") + "?email=success";
        } else {
            return "redirect:/search";
        }
    }
}