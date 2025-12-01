package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;

public record OrderDto(
    Long id,
    long totalPriceCents,
    Instant createdAt
) {
}
