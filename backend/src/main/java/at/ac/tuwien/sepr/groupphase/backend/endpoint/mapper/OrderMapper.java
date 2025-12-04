package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "ticketIds", expression = "java(order.getTickets().stream().map(t -> t.getId()).toList())")
    OrderDto orderToOrderDto(Order order);

    List<OrderDto> orderListToOrderDtoList(List<Order> orders);
}
