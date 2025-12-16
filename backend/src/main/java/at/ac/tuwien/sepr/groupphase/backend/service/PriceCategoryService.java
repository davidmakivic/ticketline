package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryUpdateDto;

import java.util.List;

public interface PriceCategoryService {

    /**
     * Retrieves all price categories as DTOs.
     *
     * @return list of PriceCategoryDto
     */
    List<PriceCategoryDto> findAll();

    /**
     * Retrieves a price category by its ID.
     *
     * @param id ID of the price category
     * @return PriceCategoryDto or null if not found
     */
    PriceCategoryDto findById(Long id);

    /**
     * Updates a price category.
     *
     * @param id   the id of the priceCategory to be updated
     * @param priceCategory the dto to update the previous priceCategory with
     * @return the saved PriceCategoryDto
     */
    PriceCategoryDto update(Long id, PriceCategoryUpdateDto priceCategory);


    /**
     * Creates a price category.
     *
     * @param dto the PriceCategoryCreateDto to create
     * @return the saved PriceCategoryDto
     */
    PriceCategoryDto create(PriceCategoryCreateDto dto);


    /**
     * Deletes a price category by ID.
     *
     * @param id ID of the price category to delete
     */
    void delete(Long id);
}
