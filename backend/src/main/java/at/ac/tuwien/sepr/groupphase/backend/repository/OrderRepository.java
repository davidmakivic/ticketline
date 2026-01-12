package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves all orders sorted by creation date in descending order.
     *
     * @return a list of orders, newest first
     */
    @Query("""
                select distinct o
                from Order o
                left join fetch o.user
                left join fetch o.tickets
                order by o.createdAt desc
                """)
    List<Order> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves all orders belonging to a specific user, sorted by creation date in descending order.
     *
     * @param userId the ID of the user whose orders should be retrieved
     * @return a list of orders for that user, newest first
     */
    @Query("""
                select distinct o
                from Order o
                left join fetch o.user
                left join fetch o.tickets
                where o.user.userId = :userId
                order by o.createdAt desc
                """)
    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * Needed for getOrder(id) to avoid LazyInitializationException when mapping tickets.
     */
    @Query("""
                select o
                from Order o
                left join fetch o.user
                left join fetch o.tickets
                where o.id = :id
                """)
    java.util.Optional<Order> findByIdWithTickets(@Param("id") Long id);
}
