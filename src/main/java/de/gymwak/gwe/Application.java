package de.gymwak.gwe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	/**
	 * Diese main Methode startet beim Ausfuehren einen Tomcat Server auf dem
	 * die Spring Anwendung getestet werden kann (Port 8080)
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
