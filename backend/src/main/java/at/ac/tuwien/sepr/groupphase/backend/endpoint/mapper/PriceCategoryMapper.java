package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceCategoryMapper {

    /**
     * Converts a PriceCategory entity to its corresponding PriceCategoryDto.
     *
     * @param priceCategory the PriceCategory entity to convert
     * @return the corresponding PriceCategoryDto
     */
    PriceCategoryDto priceCategoryToPriceCategoryDto(PriceCategory priceCategory);

    /**
     * Converts a PriceCategoryDto to its corresponding PriceCategory entity.
     *
     * @param priceCategoryDto the PriceCategoryDto to convert
     * @return the corresponding PriceCategory entity
     */
    PriceCategory priceCategoryDtoToPriceCategory(PriceCategoryDto priceCategoryDto);

    /**
     * Converts a list of PriceCategory entities to a list of PriceCategoryDto objects.
     *
     * @param priceCategories the list of PriceCategory entities to convert
     * @return the corresponding list of PriceCategoryDto objects
     */
    List<PriceCategoryDto> priceCategoryListToPriceCategoryDtoList(List<PriceCategory> priceCategories);

}
