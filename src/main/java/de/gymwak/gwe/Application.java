package de.gymwak.gwe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	/**
	 * Diese main Methode startet beim Ausfuehren einen Tomcat Server auf dem
	 * die Spring Anwendung getestet werden kann (Port 8080)
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
