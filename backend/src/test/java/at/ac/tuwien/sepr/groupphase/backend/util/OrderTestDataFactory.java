package at.ac.tuwien.sepr.groupphase.backend.util;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderMerchItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderRewardItemDto;

import java.time.Instant;
import java.util.List;

public class OrderTestDataFactory {

    public static OrderDto create(Long userId, long price, long pricePoints, List<Long> ticketIds) {
        return new OrderDto(
            null,
            userId,
            price,
            pricePoints,
            Instant.now(),
            ticketIds,
            List.<OrderMerchItemDto>of(),
            List.<OrderRewardItemDto>of()
        );
    }

    public static OrderDto createSimple(Long userId) {
        return new OrderDto(
            null,
            userId,
            1000L,
            0L,
            Instant.now(),
            List.of(),
            List.<OrderMerchItemDto>of(),
            List.<OrderRewardItemDto>of()
        );
    }
}
