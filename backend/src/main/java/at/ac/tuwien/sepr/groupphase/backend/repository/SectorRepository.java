package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    /**
     * Finds all sectors belonging to a specific hall.
     *
     * @param hallId the ID of the hall
     * @return a list of sectors belonging to the given hall
     */
    List<Sector> findByHallId(Long hallId);

    /**
     * Finds a sector by hall ID and sector key.
     *
     * @param hallId    the ID of the hall
     * @param sectorKey the unique key of the sector
     * @return the matching Sector entity
     */
    Sector findByHallIdAndSectorKey(Long hallId, String sectorKey);

}
