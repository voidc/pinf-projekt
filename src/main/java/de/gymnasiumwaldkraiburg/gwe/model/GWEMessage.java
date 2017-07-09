package de.gymnasiumwaldkraiburg.gwe.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class GWEMessage implements Serializable {

    private long recipientId = -1;

    private long eventId = -1;

    private int recipientsYear = -1;

    @NotEmpty(message = "Text is required.")
    private String content;

    private String subject = "";

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

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRecipientsYear() {
        return recipientsYear;
    }

    public void setRecipientsYear(int recipientsGraduationYear) {
        this.recipientsYear = recipientsGraduationYear;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private static final long serialVersionUID = -7772217675815839488L;

}
