package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

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
    List<OrderDto> getOrdersByUser(Long userId);

    /**
     * Creates a new order for the currently authenticated user.
     * The order is built using the given list of ticket IDs.
     *
     * @param createDto the DTO containing the ticket IDs required to build the order
     * @return the newly created order as OrderDto
     */
    OrderDto createOrder(OrderCreateDto createDto) throws ValidationException, ConflictException;


    /**
     * Updates an existing order by replacing its associated ticket list.
     *
     * @param id the ID of the order to update
     * @param updateDto the DTO containing the new list of ticket IDs
     * @return the updated order as OrderDto
     */
    OrderDto updateOrder(long id, OrderUpdateDto updateDto);
}
