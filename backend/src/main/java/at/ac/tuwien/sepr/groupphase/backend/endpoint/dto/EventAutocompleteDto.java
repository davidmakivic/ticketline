package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

public interface EventAutocompleteDto {
    Long getId();

    String getTitle();

    EventType getCategory();

    Integer getDurationInMinutes();
}
