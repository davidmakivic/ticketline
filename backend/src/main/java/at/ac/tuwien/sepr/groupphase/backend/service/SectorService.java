package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorUpdateDto;

import java.util.List;

public interface SectorService {


    /**
     * Find a single sector by id.
     *
     * @param id the id of the sector
     * @return the sector
     */
    SectorDto findById(Long id);

    /**
     * Find a list of all sectors.
     *
     * @return a list of all sector entries
     */
    List<SectorDto> findAll();


    /**
     * Find all sectors of a hall.
     *
     * @param hallId the id of the hall
     * @return a list of all sector entries in the given hall
     */
    List<SectorDto> findByHallId(Long hallId);

    /**
     * Create a single sector.
     *
     * @param sector the sector to be created
     * @return the created sector
     */
    SectorDto create(SectorCreateDto sector);


    /**
     * Update a sector.
     *
     * @param id the id of the sector to be updated
     * @param sector the sector to update the previous sector
     * @return the updated sector
     */
    SectorDto update(Long id, SectorUpdateDto sector);



    /**
     * Delete a single sector by id.
     *
     * @param id the id of the sector
     */
    void delete(Long id);
}
