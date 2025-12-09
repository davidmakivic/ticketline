package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;

import java.util.List;

public interface SeatService {


    /**
     * Find a single seat by id.
     *
     * @param id the id of the seat
     * @return the seat entry
     */
    SeatDto findById(Long id);


    /**
     * Find a list of all seats.
     *
     * @return a list of all seats entries
     */
    List<SeatDto> findAll();


    /**
     * Delete a single seat by id.
     *
     * @param id the id of the seat
     */
    void delete(Long id);

    /**
     * Create a single seat.
     *
     * @param seat the seat to be created
     * @return the created seat
     */
    SeatDto create(SeatCreateDto seat);


    /**
     * Update a seat.
     *
     * @param id   the id of the seat to be updated
     * @param seat the seat to update the previous seat
     * @return the updated seat
     */
    SeatDto update(Long id, SeatUpdateDto seat);


    /**
     * Find all seats of a sector.
     *
     * @param sectorId the id of the sector
     * @return a list of all seat entries in the given sector
     */
    List<SeatDto> findBySectorId(Long sectorId);

}
