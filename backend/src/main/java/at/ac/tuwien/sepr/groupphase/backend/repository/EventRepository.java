package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Event> findByAnyTitle(@Param("title") String title);


    @Query("""
            SELECT e.id AS id, e.title AS title, e.category AS category, e.durationMinutes AS durationInMinutes
            FROM Event e
            WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))
        """)
    List<EventAutocompleteDto> findEventAutocompleteDto(@Param("title") String title, Pageable pageable);


    @Query("""
        select new at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto(e.id, e.title, e.category,
          sum(case when t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.PURCHASED then 1 else 0 end))
        from Performance p
          join p.event e
          left join Ticket t on t.performance = p
        where p.startTime >= :startOfMonth
          and p.startTime < :startOfNextMonth
          and (:allCategories = true or e.category = :category)
        group by e.id, e.title, e.category
        order by e.title, sum(case when t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.PURCHASED then 1 else 0 end) desc
        """)
    List<EventTop10Dto> findTopEventsOfMonth(
        LocalDateTime startOfMonth,
        LocalDateTime startOfNextMonth,
        EventType category,
        boolean allCategories,
        Pageable pageable
    );

}