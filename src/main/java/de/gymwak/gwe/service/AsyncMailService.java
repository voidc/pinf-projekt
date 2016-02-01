package de.gymwak.gwe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncMailService {
	private JavaMailSender mailSender;
	
	@Autowired
	public AsyncMailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Async
	public void sendMail(MimeMessagePreparator mail) {
		try {
			mailSender.send(mail);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}

}
