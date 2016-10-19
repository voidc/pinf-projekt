package de.gymwak.gwe.mvc;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice @Order(Ordered.HIGHEST_PRECEDENCE) public class ErrorAdvice {

	@ExceptionHandler(NoHandlerFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND) public ModelAndView handle404(
			NoHandlerFoundException ex) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errmsg", "404 - Seite nicht gefunden");
		return mav;
	}

	@ExceptionHandler(Throwable.class) public ModelAndView handleException(Throwable ex) {
		ex.printStackTrace();
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("exception", ex);
		mav.addObject("stackTrace", ex.getStackTrace());
		return mav;
	}

}
