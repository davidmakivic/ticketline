package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import java.util.List;

public interface OrderService {

    /**
     * Retrieves all orders sorted by creation date (newest first).
     *
     * @return a list of all orders
     */
    List<OrderDto> getAllOrders();

    /**
     * Retrieves a single order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the corresponding OrderDto
     */
    OrderDto getOrder(long id);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of orders belonging to the given user
     */
    List<OrderDto> getOrdersByUser(Integer userId);
}
