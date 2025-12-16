package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

    /**
     * Find all sectors of a hall.
     *
     * @return a list of all sectors of the given hall
     */
    List<Sector> findByHallId(Long hallId);

    Sector findByHallIdAndSectorKey(Long hallId, String sectorKey);

}
