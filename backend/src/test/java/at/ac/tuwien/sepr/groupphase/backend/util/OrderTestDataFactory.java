package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;

import java.time.Instant;
import java.util.List;

public class OrderTestDataFactory {

    public static OrderDto create(Long userId, long price, List<Long> ticketIds) {
        return new OrderDto(
            null,
            userId,
            price,
            Instant.now(),
            ticketIds
        );
    }

    public static OrderDto createSimple(Long userId) {
        return new OrderDto(
            null,
            userId,
            1000L,
            Instant.now(),
            List.of()
        );
    }
}
