package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventTop10Dto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Event> findByAnyTitle(@Param("title") String title);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.artists")
    List<Event> findAllWithArtists();

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.performances WHERE e.id = :id")
    Optional<Event> findByIdWithPerformances(@Param("id") Long id);


    @Query("""
            SELECT e.id AS id, e.title AS title, e.category AS category, e.durationMinutes AS durationInMinutes
            FROM Event e
            WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))
        """)
    List<EventAutocompleteDto> findEventAutocompleteDto(@Param("title") String title, Pageable pageable);

    @Query("""
    SELECT DISTINCT e
    FROM Event e
    LEFT JOIN FETCH e.performances perf
    LEFT JOIN FETCH perf.hall hall
    LEFT JOIN FETCH hall.venue venue
    WHERE
      (:title IS NULL
        OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:artist IS NULL
        OR EXISTS (
          SELECT 1 FROM e.artists a
          WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :artist, '%'))
            OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :artist, '%'))
            OR LOWER(a.stageName) LIKE LOWER(CONCAT('%', :artist, '%'))
        ))
      AND (:location IS NULL
        OR EXISTS (
          SELECT 1 FROM e.performances p
          JOIN p.hall h
          JOIN h.venue v
          WHERE LOWER(v.city) LIKE LOWER(CONCAT('%', :location, '%'))
            OR LOWER(v.street) LIKE LOWER(CONCAT('%', :location, '%'))
            OR LOWER(v.country) LIKE LOWER(CONCAT('%', :location, '%'))
            OR LOWER(v.postalCode) LIKE LOWER(CONCAT('%', :location, '%'))
        ))
      AND (:eventType IS NULL
        OR e.category = :eventType)
      AND (:startDate IS NULL
        OR EXISTS (
          SELECT 1 FROM e.performances p
          WHERE CAST(p.startTime AS DATE) = CAST(:startDate AS DATE)
        ))
      AND (:durationMinutes IS NULL
        OR (e.durationMinutes >= :durationMinutes - 30
            AND e.durationMinutes <= :durationMinutes + 30))""")
    Page<Event> findByAdvancedFilters(
        @Param("title") String title,
        @Param("artist") String artist,
        @Param("location") String location,
        @Param("eventType") EventType eventType,
        @Param("startDate") Date startDate,
        @Param("durationMinutes") Integer durationMinutes,
        Pageable pageable
    );

    @Query("SELECT e FROM Event e ORDER BY e.id")
    Page<Event> findAllPaginated(Pageable pageable);





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