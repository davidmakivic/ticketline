package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;

import java.util.List;

public interface VenueService {

    /**
     * Creates a new Venue with the attributes specifies in venueDto
     * @param venueDto the venue to be created
     * @return a dto of the created venue
     */
    VenueDto create(VenueDto venueDto);

    /**
     * Finds a venue specifies by its Id
     * @param id of the desired venue
     * @return a dto of the found venue
     */
    VenueDto findById(Long id);

    /**
     * Retrieves all venues currently found in the persistence layer
     * @return a List of dtos of all the found venues
     */
    List<VenueDto> findAll();

    /**
     * Deletes a venue specified by its Id
     * @param id of the venue to be deleted
     */
    void delete(Long id);

    /**
     * Updates the venue from the specified Id with the attributs fro the dto
     * @param id of the venue to be updated
     * @param updatedVenue the new venue
     * @return a dto of the newly updated venue
     */
    VenueDto update(Long id, VenueDto updatedVenue);
}
