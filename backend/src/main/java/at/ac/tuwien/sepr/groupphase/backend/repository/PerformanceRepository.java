package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>, JpaSpecificationExecutor<Performance> {

    List<Performance> findByEventId(Long eventId);

    @Query("SELECT DISTINCT p FROM Performance p")
    List<Performance> findAllIds(Pageable pageable);

    @Query("""
        SELECT DISTINCT p FROM Performance p
        JOIN FETCH p.hall h
        JOIN FETCH h.sectors
        WHERE p.id IN :ids
        """)
    List<Performance> findByIdsWithHallAndSectors(@Param("ids") List<Long> ids);


    @Query("SELECT p FROM Performance p JOIN FETCH p.event JOIN FETCH p.hall")
    Page<Performance> findAllWithDetails(Pageable pageable);


    @Query("""
        SELECT DISTINCT p FROM Performance p
        JOIN FETCH p.hall h
        JOIN FETCH h.sectors
        """)
    List<Performance> findAllWithHallAndSectors(Pageable pageable);


}
