package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;

import java.util.List;

public interface OrderService {

    /**
     * Gibt alle Bestellungen sortiert nach Datum zurück.
     */
    List<OrderDto> getAllOrders();

    /**
     * Holt eine Order anhand der ID.
     */
    OrderDto getOrder(long id);

    /**
     * Holt alle Bestellungen eines bestimmten Users.
     */
    List<OrderDto> getOrdersByUser(Long userId);
}
