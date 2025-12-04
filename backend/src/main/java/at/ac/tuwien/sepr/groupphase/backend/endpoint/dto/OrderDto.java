package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.Instant;
import java.util.List;

public record OrderDto(
    Long id,
    Long userId,
    long totalPriceCents,
    Instant createdAt,
    List<Long> ticketIds
) {}
