package de.gymwak.gwe.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class GWEMessage implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Email(message = "Please provide a valid email address.")
	@NotEmpty(message = "Email is required.")
	@Column(unique = true, nullable = false)
	private String email;

	@NotEmpty(message = "First name is required.")
	private String message;

	public GWEMessage() {
	}

	public GWEMessage(GWEMessage message) {
		this.id = message.getId();
		this.email = message.getEmail();
		this.message = message.getMessage();
	}

	public GWEMessage(String email, String message) {
		this.email = email;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private static final long serialVersionUID = 822638085876787423L;

}
