package de.gymwak.gwe.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class GWEEvent implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty(message = "Name is required.")
	@Column(unique = true, nullable = false)
	private String name;

	@NotNull()
	private long organizerId = -1;

	@NotEmpty(message = "Description is required.")
	private String description;

//	@NotEmpty(message = "Description is required.")
//	private Date date;

	@NotEmpty(message = "Description is required.")
	private String place;

	private long[] participants;

	public GWEEvent() {
	}

	public GWEEvent(GWEEvent event) {
		this.id = event.id;
		this.name = event.name;
		this.organizerId = event.organizerId;
		this.description = event.description;
		this.participants = event.participants;
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

	public long getOrganizerId() {
		return organizerId;
	}

	public void setOrganizerId(long organizerId) {
		this.organizerId = organizerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public long[] getParticipants() {
		return participants;
	}

	public void setParticipants(long[] participants) {
		this.participants = participants;
	}

	public boolean hasParticipant(Long userId) {
		// TODO Auto-generated method stub
		return false;
	}

	private static final long serialVersionUID = -3514681042696071509L;
}
