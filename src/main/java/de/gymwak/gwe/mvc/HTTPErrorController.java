package de.gymwak.gwe.mvc;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HTTPErrorController {

	@ExceptionHandler(Exception.class)
	public String handleException() {
		return "error";
	}

}
