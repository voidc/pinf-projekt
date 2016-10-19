package de.gymwak.gwe.mvc;

import de.gymwak.gwe.service.AsyncMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/impressum")
public class ImpressumController {
    private AsyncMailService mailService;

    @Value("${EMAIL_USER}")
    private String adminMail;

    @Autowired
    public ImpressumController(AsyncMailService mailService) {
        this.mailService = mailService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "impressum";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String reportBug(@RequestParam String email,
                            @RequestParam String message, RedirectAttributes rAttr) {
        if (email.isEmpty() || message.isEmpty()) {
            rAttr.addFlashAttribute("email", email);
            rAttr.addFlashAttribute("message", message);
            return "redirect:/impressum?error#feedback";
        }

        String content = String.format("Feedback von %s\n\n%s", email, message);

        mailService.sendMail(mime -> {
            MimeMessageHelper mail = new MimeMessageHelper(mime);
            mail.setSubject("Feedback");
            mail.setFrom(adminMail, "GWE");
            mail.setText(content);
            mail.addTo(adminMail);
        });

        return "redirect:/impressum?success#feedback";
    }

}
