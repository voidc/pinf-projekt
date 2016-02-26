package de.gymwak.gwe.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

	@NotNull(message = "Graduation type is required.")
	private GraduationType graduationType;

	@Min(1940)
	private int graduationYear;

	@NotEmpty(message = "Occupation is required.")
	private String occupation;

	@Enumerated(EnumType.STRING)
	private Discipline discipline;
	
	private String resetToken;
	
	private Timestamp resetTokenIssued;
	
	private boolean activated;

	private String activationToken;

	public GWEUser() {
	}

	public GWEUser(GWEUser user) {
		this.id = user.id;
		this.email = user.email;
		this.password = user.password;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.graduationType = user.graduationType;
		this.graduationYear = user.graduationYear;
		this.occupation = user.occupation;
		this.discipline = user.discipline;
		this.activated = user.activated;
	}

	public void applyUserEdit(GWEUserEdit edit) {
		this.email = edit.getEmail();
		this.firstName = edit.getFirstName();
		this.lastName = edit.getLastName();
		this.graduationType = edit.getGraduationType();
		this.graduationYear = edit.getGraduationYear();
		this.occupation = edit.getOccupation();
		this.discipline = edit.getDiscipline();
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

	public GraduationType getGraduationType() {
		return graduationType;
	}

	public void setGraduationType(GraduationType graduationType) {
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

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Timestamp getResetTokenIssued() {
		return resetTokenIssued;
	}

	public void setResetTokenIssued(Timestamp resetTokenIssued) {
		this.resetTokenIssued = resetTokenIssued;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	private static final long serialVersionUID = 2738859149330833739L;

	public enum Discipline {
		D("Deutsch"),
		E("Englisch"),
		F("Französisch"),
		L("Latein"),
		KU("Kunst"),
		MU("Musik"),
		GEO("Geographie"),
		GSK("Geschichte / Sozialk."),
		WR("Wirtschaft / Recht"),
		RE("Religion / Ethik"),
		M("Mathematik"),
		BIO("Biologie"),
		CH("Chemie"),
		PH("Physik"),
		INF("Informatik"),
		SP("Sport");

		public final String desc;
		Discipline(String desc) {
			this.desc = desc;
		}
	}

	public enum GraduationType {
		ABITUR_WALDKRAIBURG(0, "Abitur am Gymnasium Waldkraiburg"),
		ABITUR(1, "Abitur an einem anderem Gymnasium"),
		SONSTIGER_ABSCHLUSS(2, "Kein / anderer Abschluss");

		public final int id;
		public final String desc;

		GraduationType(int id, String desc) {
			this.id = id;
			this.desc = desc;
		}
	}

}
