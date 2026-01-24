package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MerchandiseService {

    /**
     * Retrieves all merchandise items.
     *
     * @return a list of all MerchandiseDto objects
     */
    List<MerchandiseDto> findAll();

    /**
     * Retrieves a merchandise item by its ID.
     *
     * @param id the ID of the merchandise
     * @return the corresponding MerchandiseDto
     */
    MerchandiseDto findById(Long id);

    /**
     * Saves a merchandise item.
     *
     * @param merchandiseDto the merchandise data to save
     * @return the saved MerchandiseDto
     */
    MerchandiseDto save(MerchandiseDto merchandiseDto);

    /**
     * Deletes a merchandise item by its ID.
     *
     * @param id the ID of the merchandise to delete
     */
    void delete(Long id);

    /**
     * Retrieves the image of a merchandise item.
     *
     * @param id the ID of the merchandise
     * @return a ResponseEntity containing the image data
     */
    ResponseEntity<byte[]> getMerchandiseImage(Long id);

}
