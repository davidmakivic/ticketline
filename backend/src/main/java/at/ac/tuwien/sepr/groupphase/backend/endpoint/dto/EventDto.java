package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

public class EventDto {
    private int id;
    private String title;
    private String description;
    private EventType category;
    private int durationMinutes;

    public EventDto(int id, String title, String description, EventType category, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationMinutes = durationMinutes;
    }

    public EventDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getCategory() {
        return category;
    }

    public void setCategory(EventType category) {
        this.category = category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
