package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats of a sector.
     *
     * @return a list of all seats of the given sector
     */
    List<Seat> findBySectorId(long sectorId);

    Seat findBySectorIdAndRowNumberAndSeatNumber(Long sectorId, int rowNumber, int seatNumber);
}
