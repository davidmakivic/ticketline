package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Converts an Order entity to its corresponding OrderDto.
     *
     * @param order the Order entity to convert
     * @return the corresponding OrderDto
     */
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "ticketIds",
        expression = "java(order.getTickets().stream().map(t -> t.getId()).toList())")
    OrderDto orderToOrderDto(Order order);

    /**
     * Converts a list of Order entities to a list of OrderDto objects.
     *
     * @param orders the list of Order entities to convert
     * @return the corresponding list of OrderDto objects
     */
    List<OrderDto> orderListToOrderDtoList(List<Order> orders);
}
