package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;

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
     * Saves or updates a price category.
     *
     * @param dto the PriceCategoryDto to save
     * @return the saved PriceCategoryDto
     */
    PriceCategoryDto save(PriceCategoryDto dto);

    /**
     * Deletes a price category by ID.
     *
     * @param id ID of the price category to delete
     */
    void delete(Long id);
}
