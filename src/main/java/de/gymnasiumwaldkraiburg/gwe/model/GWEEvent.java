package de.gymnasiumwaldkraiburg.gwe.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.unbescape.html.HtmlEscape;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class GWEEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Name is required.")
    private String name;

    @NotNull()
    @ManyToOne()
    private GWEUser organizer;

    @NotEmpty(message = "Description is required.")
    @Column(columnDefinition = "TEXT")
    private String description;

    private java.sql.Timestamp time;

    @NotEmpty(message = "Place is required.")
    private String place;

    @ManyToMany()
    @OrderColumn
    private List<GWEUser> participants;

    public GWEEvent() {
    }

    public GWEEvent(GWEEvent event) {
        this.id = event.id;
        this.name = event.name;
        this.organizer = event.organizer;
        this.description = event.description;
        this.participants = event.participants;
        this.place = event.place;
        this.time = event.time;
    }

    public void applyEventEdit(GWEEventEdit edit) {
        this.name = edit.getName();
        this.organizer = edit.getOrganizer();
        this.description = edit.getDescription();
        this.participants = edit.getParticipants();
        this.place = edit.getPlace();
        this.time = edit.getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GWEUser getOrganizer() {
        return organizer;
    }

    public void setOrganizer(GWEUser organizer) {
        this.organizer = organizer;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        int i = description.indexOf('\n');
        return description.substring(0, i >= 0 ? i : description.length());
    }

    public String getFullDescription() {
        return HtmlEscape.escapeHtml4Xml(description)
                .replace(System.getProperty("line.separator"), "<br />");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<GWEUser> getParticipants() {
        return participants;
    }

    public void setParticipants(List<GWEUser> participants) {
        this.participants = participants;
    }

    public boolean hasParticipant(GWEUser user) {
        return participants.contains(user);
    }

    public boolean isOver() {
        return time.getTime() < System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof GWEEvent && ((GWEEvent) obj).id == this.id;
    }

    private static final long serialVersionUID = -3514681042696071509L;
}
