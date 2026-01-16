package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        select distinct o
        from Order o
        left join fetch o.user
        left join fetch o.tickets
        left join fetch o.merchItems mi
        left join fetch mi.variant v
        left join fetch v.merchandise m
        order by o.createdAt desc
        """)
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("""
        select distinct o
        from Order o
        left join fetch o.user
        left join fetch o.tickets
        left join fetch o.merchItems mi
        left join fetch mi.variant v
        left join fetch v.merchandise m
        where o.user.userId = :userId
        order by o.createdAt desc
        """)
    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("""
        select distinct o
        from Order o
        left join fetch o.user
        left join fetch o.tickets
        left join fetch o.merchItems mi
        left join fetch mi.variant v
        left join fetch v.merchandise m
        where o.id = :id
        """)
    java.util.Optional<Order> findByIdWithTickets(@Param("id") Long id);
}
