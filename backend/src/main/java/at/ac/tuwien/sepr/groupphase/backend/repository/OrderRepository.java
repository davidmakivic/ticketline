package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves all orders sorted by creation date in descending order.
     *
     * @return a list of orders, newest first
     */
    List<Order> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves all orders belonging to a specific user, sorted by creation date in descending order.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of orders for that user, newest first
     */
    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(Integer userId);

}
