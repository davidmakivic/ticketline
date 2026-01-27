package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.type.Roles;
import at.ac.tuwien.sepr.groupphase.backend.type.UserStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    private ApplicationUser createUser() {
        ApplicationUser user = new ApplicationUser();
        user.setUserId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("pw");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Roles.USER);
        user.setUserStatus(UserStatus.UNLOCKED);
        return user;
    }

    @Test
    void orderToOrderDto_shouldMapCorrectly() {
        ApplicationUser user = createUser();

        Ticket t1 = new Ticket();
        t1.setId(10L);

        Ticket t2 = new Ticket();
        t2.setId(20L);

        Order order = new Order(user, 1500, 200);

        order.getTickets().add(t1);
        order.getTickets().add(t2);

        OrderDto dto = orderMapper.orderToOrderDto(order);

        assertEquals(1L, dto.getUserId());
        assertEquals(1500, dto.getTotalPriceCents());
        assertEquals(2, dto.getTicketIds().size());
    }

    @Test
    void orderListToOrderDtoList_shouldMapListCorrectly() {
        ApplicationUser user = createUser();

        Order o1 = new Order(user, 1000, 200);
        Order o2 = new Order(user, 2000, 300);

        List<OrderDto> dtos = orderMapper.orderListToOrderDtoList(List.of(o1, o2));

        assertEquals(2, dtos.size());
    }
}
