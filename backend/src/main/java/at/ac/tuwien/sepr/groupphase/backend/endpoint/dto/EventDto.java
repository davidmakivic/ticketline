package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

import java.util.HashSet;
import java.util.Set;

public class EventDto {
    private Long id;
    private String title;
    private String description;
    private EventType category;
    private Integer durationMinutes;
    private byte[] imageData;
    private String imageContentType;
    private Set<Artist> artists = new HashSet<>();
    private Set<PerformanceDto> performances = new HashSet<>();

    public EventDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public Set<PerformanceDto> getPerformances() {
        return performances;
    }

    public void setPerformances(Set<PerformanceDto> performances) {
        this.performances = performances;
    }
}
