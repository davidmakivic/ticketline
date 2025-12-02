package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        return new OrderDto(
            order.getId(),
            order.getUser().getUserId().longValue(),
            order.getTotalPriceCents(),
            order.getCreatedAt(),
            order.getTickets().stream().map(t -> t.getId()).toList()
        );
    }
}
