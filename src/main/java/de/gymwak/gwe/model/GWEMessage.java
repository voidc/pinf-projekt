package de.gymwak.gwe.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class GWEMessage implements Serializable {

	private long recipientId;

	@NotEmpty(message = "Text is required.")
	private String content;

	public GWEMessage() {
	}

	public GWEMessage(GWEMessage message) {
		this.recipientId = message.recipientId;
	}

	public long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(long recipientId) {
		this.recipientId = recipientId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private static final long serialVersionUID = -7772217675815839488L;

}
