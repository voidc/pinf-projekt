package de.gymwak.gwe.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

public class GWEUserEdit implements Serializable {

	@Email(message = "Please provide a valid email address.")
	@NotEmpty(message = "Email is required.")
	private String email;

	@NotEmpty(message = "First name is required.")
	private String firstName;

	@NotEmpty(message = "Last name is required.")
	private String lastName;

	@NotNull(message = "Graduation type is required.")
	private GWEUser.GraduationType graduationType;

	@Min(1940)
	private int graduationYear;

	@NotEmpty(message = "Occupation is required.")
	private String occupation;

	private Collection<GWEUser.Discipline> disciplines;

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

	public GWEUser.GraduationType getGraduationType() {
		return graduationType;
	}

	public void setGraduationType(GWEUser.GraduationType graduationType) {
		this.graduationType = graduationType;
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

	public Collection<GWEUser.Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(Collection<GWEUser.Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	private static final long serialVersionUID = -6037756829879653917L;

}
