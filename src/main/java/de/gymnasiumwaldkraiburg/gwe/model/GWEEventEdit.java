package de.gymnasiumwaldkraiburg.gwe.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class GWEEventEdit implements Serializable {

    @NotEmpty(message = "Name is required.")
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull()
    @ManyToOne()
    private GWEUser organizer;

    @NotEmpty(message = "Description is required.")
    //@Lob (description sollte mehr al 255 Zeichen haben können)
    @Size(max = 1000)
    private String description;

    private java.sql.Timestamp time;

    @NotEmpty(message = "Place is required.")
    private String place;

    @ManyToMany()
    @OrderColumn
    private List<GWEUser> participants;

    public GWEEventEdit() {
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

    private static final long serialVersionUID = -3514681042696071509L;
}
