package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

public class EventTestDataFactory {
    public static EventDto create(int id, EventType type) {
        EventDto dto = new EventDto();
        dto.setId(id);
        dto.setTitle("Test Event " + id);
        dto.setDescription("Description " + id);
        dto.setCategory(type);
        dto.setDurationMinutes(30 + id);
        return dto;
    }
}
