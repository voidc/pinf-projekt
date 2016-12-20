package de.gymwak.gwe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.unbescape.html.HtmlEscape;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.EnumSet;

@Entity
public class GWEUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Email(message = "Please provide a valid email address.")
    @NotEmpty(message = "Email is required.")
    @Column(unique = true, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;

    @NotEmpty(message = "Password is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotEmpty(message = "First name is required.")
    private String firstName;

    @NotEmpty(message = "Last name is required.")
    private String lastName;

    @NotNull(message = "Graduation type is required.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private GraduationType graduationType;

    @Min(1940)
    private int graduationYear;

    @NotEmpty(message = "Occupation is required.")
    @Column(columnDefinition = "TEXT")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String occupation;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int disciplines;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String resetToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Timestamp resetTokenIssued;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean activated;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
        this.disciplines = user.disciplines;
        this.activated = user.activated;
    }

    public void applyUserEdit(GWEUserEdit edit) {
        this.email = edit.getEmail();
        this.firstName = edit.getFirstName();
        this.lastName = edit.getLastName();
        this.graduationType = edit.getGraduationType();
        this.graduationYear = edit.getGraduationYear();
        this.occupation = edit.getOccupation();
        setDisciplines(edit.getDisciplines());
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

    public String getFullName() {
        return firstName + " " + lastName;
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

    public String getShortOccupation() {
        int i = occupation.indexOf('\n');
        return occupation.substring(0, i >= 0 ? i : occupation.length());
    }

    public String getFullOccupation() {
        return HtmlEscape.escapeHtml4Xml(occupation)
                .replace(System.getProperty("line.separator"), "<br />");
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public EnumSet<Discipline> getDisciplines() {
        EnumSet<Discipline> set = EnumSet.noneOf(Discipline.class);
        for (Discipline d : Discipline.values()) {
            if (hasDiscipline(d))
                set.add(d);
        }
        return set;
    }

    public String getDisciplinesAsString() {
        if (disciplines == 0)
            return "Keine";

        String str = "";
        for (Discipline d : Discipline.values()) {
            if (hasDiscipline(d))
                str += (d.desc + ", ");
        }
        return str.substring(0, str.length() - 2);
    }

    public void setDisciplines(Collection<Discipline> disciplines) {
        this.disciplines = 0;
        if (disciplines != null) {
            for (Discipline d : disciplines) {
                this.disciplines |= d.bitMask();
            }
        }
    }

    public boolean hasDiscipline(Discipline d) {
        return (disciplines & d.bitMask()) != 0;
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

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof GWEUser && ((GWEUser) obj).id == this.id;
    }

    private static final long serialVersionUID = 2738859149330833739L;

    public enum Discipline {
        LANG("Fremdsprachen, Germanistik"), ART("Kunst, Architektur, Design"), MUSIC("Musik, Instrumente"), GEO(
                "Geographie, Geologie"), SOCIAL("Solzialwissenschaften, Historik"), LAW(
                "Jura, Recht, Öffentliche Verwaltung"), ECO("Wirtschaft, Finanzen"), PHIL(
                "Religion, Ethik, Philosophie"), MATH("Mathematik, Informatik"), BIOCHEM("Biologie, Chemie"), PHY(
                "Physik, Maschinenbau, Elektrotechnik"), MED("Sport, Medizin, Gesundheit"), PSY(
                "Psychologie, Pädagogik");

        public final String desc;

        Discipline(String desc) {
            this.desc = desc;
        }

        int bitMask() {
            return 1 << ordinal();
        }
    }

    public enum GraduationType {
        ABITUR_WALDKRAIBURG("Abitur am Gymnasium Waldkraiburg"), ABITUR(
                "Abitur an einem anderem Gymnasium"), SONSTIGER_ABSCHLUSS("Kein / anderer Abschluss");

        public final String desc;

        GraduationType(String desc) {
            this.desc = desc;
        }
    }

}
