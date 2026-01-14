package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByEventId(Long eventId);

    @Query("""
    SELECT DISTINCT p
    FROM Performance p
    LEFT JOIN FETCH p.event e
    LEFT JOIN FETCH e.artists a
    LEFT JOIN FETCH p.hall h
    LEFT JOIN FETCH h.venue v
    WHERE
      (:title IS NULL
        OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:artist IS NULL
        OR EXISTS (
          SELECT 1 FROM e.artists artist
          WHERE LOWER(artist.firstName) LIKE LOWER(CONCAT('%', :artist, '%'))
            OR LOWER(artist.lastName) LIKE LOWER(CONCAT('%', :artist, '%'))
            OR LOWER(artist.stageName) LIKE LOWER(CONCAT('%', :artist, '%'))
        ))
      AND (:location IS NULL
        OR LOWER(v.city) LIKE LOWER(CONCAT('%', :location, '%'))
        OR LOWER(v.street) LIKE LOWER(CONCAT('%', :location, '%'))
        OR LOWER(v.country) LIKE LOWER(CONCAT('%', :location, '%'))
        OR LOWER(v.postalCode) LIKE LOWER(CONCAT('%', :location, '%')))
      AND (:eventType IS NULL
        OR e.category = :eventType)
      AND (:startDate IS NULL
        OR p.startTime >= :startDate)
      AND (:durationMinutes IS NULL
        OR (e.durationMinutes >= :durationMinutes - 30
            AND e.durationMinutes <= :durationMinutes + 30))
    ORDER BY p.startTime
    """)
    Page<Performance> findByAdvancedFilters(
        @Param("title") String title,
        @Param("artist") String artist,
        @Param("location") String location,
        @Param("eventType") EventType eventType,
        @Param("startDate") java.util.Date startDate,
        @Param("durationMinutes") Integer durationMinutes,
        Pageable pageable
    );


    @Query("""
    SELECT p
    FROM Performance p
    LEFT JOIN FETCH p.event e
    LEFT JOIN FETCH p.hall h
    LEFT JOIN FETCH h.venue v
    ORDER BY p.startTime
    """)
    Page<Performance> findAllWithDetails(Pageable pageable);

}
