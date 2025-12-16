package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.type.EventType;

public record EventTop10Dto(
    Long eventId,
    String title,
    EventType category,
    Long soldTickets
) {
}
