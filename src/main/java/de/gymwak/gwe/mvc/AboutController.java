package de.gymwak.gwe.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/about")
public class AboutController {
    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "about";
    }
}
