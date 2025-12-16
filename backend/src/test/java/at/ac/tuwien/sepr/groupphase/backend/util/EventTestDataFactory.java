package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

public class EventTestDataFactory {

    public static EventDto createEventDto(String title, String description, EventType category, Integer durationMinutes) {
        EventDto dto = new EventDto();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setCategory(category);
        dto.setDurationMinutes(durationMinutes);
        dto.setImageContentType("image/jpeg");
        return dto;
    }

    public static Event createEvent(String title, String description, EventType category, Integer durationMinutes) {
        Event event = new Event(title, description, category, durationMinutes);
        return event;
    }

    public static EventDto createEventDtoWithId(Long id, String title, String description, EventType category, Integer durationMinutes) {
        EventDto dto = createEventDto(title, description, category, durationMinutes);
        dto.setId(id);
        return dto;
    }
}