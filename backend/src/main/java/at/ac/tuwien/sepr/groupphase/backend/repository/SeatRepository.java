package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats of a sector.
     *
     * @return a list of all seats of the given sector
     */
    List<Seat> findBySectorId(long sectorId);

    Seat findBySectorIdAndRowNumberAndSeatNumber(Long sectorId, int rowNumber, int seatNumber);

    List<Seat> findBySector_Hall_Id(Long hallId);

    @Query("select s from Seat s join fetch s.sector sec where sec.hall.id = :hallId")
    List<Seat> findByHallIdWithSector(@Param("hallId") Long hallId);


}
