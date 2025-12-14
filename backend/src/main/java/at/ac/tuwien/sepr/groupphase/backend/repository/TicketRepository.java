package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByPerformance_Id(Long performanceId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tickets set order_id = null where ticket_id in (:ids)", nativeQuery = true)
    void detachFromOrder(@Param("ids") List<Long> ids);
}