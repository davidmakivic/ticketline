package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventAutocompleteDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}