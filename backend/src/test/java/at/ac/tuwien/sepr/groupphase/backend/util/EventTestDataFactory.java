package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

import java.util.HashSet;

public class EventTestDataFactory {
    public static EventDto create(EventType type) {
        EventDto dto = new EventDto();
        dto.setTitle("Test Event");
        dto.setDescription("Description");
        dto.setCategory(type);
        dto.setDurationMinutes(30);
        dto.setArtists(new HashSet<>());
        return dto;
    }
}
