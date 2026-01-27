package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Converts an {@link Order} entity to an {@link OrderDto}.
     *
     * @param order the entity to convert
     * @return the converted DTO
     */
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(
        target = "ticketIds",
        expression = "java(order.getTickets() == null"
            + " ? java.util.List.of()"
            + " : order.getTickets().stream()"
            + "     .map(at.ac.tuwien.sepr.groupphase.backend.entity.Ticket::getId)"
            + "     .toList())"
    )
    @Mapping(target = "merchItems", expression = "java(java.util.List.of())")
    @Mapping(target = "rewardItems", expression = "java(java.util.List.of())")
    OrderDto orderToOrderDto(Order order);

    /**
     * Converts a list of {@link Order} entities to a list of {@link OrderDto}.
     *
     * @param orders the entities to convert
     * @return the converted DTO list
     */
    List<OrderDto> orderListToOrderDtoList(List<Order> orders);
}
