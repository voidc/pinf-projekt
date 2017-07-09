package de.gymnasiumwaldkraiburg.gwe.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class GWEBanner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Content required.")
    private String content;

    @NotNull(message = "Color required.")
    private Color color;

    private boolean dismissible;

    public GWEBanner() {

    }

    public GWEBanner(GWEBanner banner) {
        this.id = banner.id;
        this.content = banner.content;
        this.color = banner.color;
        this.dismissible = banner.dismissible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public String getFormattedContent() {
        return getContent().replace("\n", "<br />");
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isDismissible() {
        return dismissible;
    }

    public void setDismissible(boolean dismissible) {
        this.dismissible = dismissible;
    }

    public enum Color {
        DEFAULT("default"),
        SUCCESS("success"),
        INFO("info"),
        WARNING("warning"),
        DANGER("danger");

        public final String desc;

        Color(String desc) {
            this.desc = desc;
        }
    }

}
