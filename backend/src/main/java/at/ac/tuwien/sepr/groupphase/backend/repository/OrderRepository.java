package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findAllByUser_UserIdOrderByCreatedAtDesc(Long userId);

}
