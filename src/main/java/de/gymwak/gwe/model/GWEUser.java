package de.gymwak.gwe.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class GWEUser implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Email(message = "Please provide a valid email address.")
	@NotEmpty(message = "Email is required.")
	@Column(unique = true, nullable = false)
	private String email;

	@NotEmpty(message = "Password is required.")
	private String password;

	@NotEmpty(message = "First name is required.")
	private String firstName;

	@NotEmpty(message = "Last name is required.")
	private String lastName;

	@Min(-1)
	private int graduationYear;

	@NotEmpty(message = "Occupation is required.")
	private String occupation;

	public GWEUser() {
	}

	public GWEUser(GWEUser user) {
		this.id = user.id;
		this.email = user.email;
		this.password = user.password;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.graduationYear = user.graduationYear;
		this.occupation = user.occupation;
	}

	public void applyUserEdit(GWEUserEdit edit) {
		this.email = edit.getEmail();
		this.firstName = edit.getFirstName();
		this.lastName = edit.getLastName();
		this.graduationYear = edit.getGraduationYear();
		this.occupation = edit.getOccupation();
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	private static final long serialVersionUID = 2738859149330833739L;

}
