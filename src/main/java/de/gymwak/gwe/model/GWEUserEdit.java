package de.gymwak.gwe.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import java.io.Serializable;

public class GWEUserEdit implements Serializable {

	@Email(message = "Please provide a valid email address.")
	@NotEmpty(message = "Email is required.")
	private String email;

	@NotEmpty(message = "First name is required.")
	private String firstName;

	@NotEmpty(message = "Last name is required.")
	private String lastName;

	@Min(1940)
	private int graduationYear;

	@NotEmpty(message = "Occupation is required.")
	private String occupation;

	@Enumerated(EnumType.STRING)
	private GWEUser.Discipline discipline;

	public GWEUserEdit() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public int getGraduationYear() {
		return graduationYear;
	}

	public void setGraduationYear(int graduationYear) {
		this.graduationYear = graduationYear;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public GWEUser.Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(GWEUser.Discipline discipline) {
		this.discipline = discipline;
	}

	private static final long serialVersionUID = -6037756829879653917L;

}
