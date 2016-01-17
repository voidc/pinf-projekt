package de.gymwak.gwe.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class GWEUserEdit implements Serializable {

	@Email(message = "Please provide a valid email address.")
	@NotEmpty(message = "Email is required.")
	private String email;
	
	@NotEmpty(message = "First name is required.")
	private String firstName;

	@NotEmpty(message = "Last name is required.")
	private String lastName;

	public GWEUserEdit() {
	}

	public GWEUserEdit(GWEUserEdit user) {
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.email = user.email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private static final long serialVersionUID = -6037756829879653917L;

}
