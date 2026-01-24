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
     * Finds all seats belonging to a specific sector.
     *
     * @param sectorId the ID of the sector
     * @return a list of seats belonging to the given sector
     */
    List<Seat> findBySectorId(long sectorId);

    /**
     * Finds a specific seat by sector, row number and seat number.
     *
     * @param sectorId  the ID of the sector
     * @param rowNumber the row number of the seat
     * @param seatNumber the seat number
     * @return the matching Seat entity
     */
    Seat findBySectorIdAndRowNumberAndSeatNumber(Long sectorId, int rowNumber, int seatNumber);

    /**
     * Finds all seats belonging to a specific hall.
     *
     * @param hallId the ID of the hall
     * @return a list of seats belonging to the given hall
     */
    List<Seat> findBySector_Hall_Id(Long hallId);

    /**
     * Finds all seats of a hall and eagerly fetches their associated sector.
     *
     * @param hallId the ID of the hall
     * @return a list of seats with their sector loaded
     */
    @Query("select s from Seat s join fetch s.sector sec where sec.hall.id = :hallId")
    List<Seat> findByHallIdWithSector(@Param("hallId") Long hallId);


}
