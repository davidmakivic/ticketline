package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseVariantDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MerchandiseMapper {

    /**
     * Converts a Merchandise entity to its corresponding MerchandiseDto.
     *
     * @param merchandise the Merchandise entity to convert
     * @return the corresponding MerchandiseDto
     */
    MerchandiseDto merchandiseToMerchandiseDto(Merchandise merchandise);

    /**
     * Converts a MerchandiseDto to its corresponding Merchandise entity.
     *
     * @param merchandiseDto the MerchandiseDto to convert
     * @return the corresponding Merchandise entity
     */
    Merchandise merchandiseDtoToMerchandise(MerchandiseDto merchandiseDto);

    /**
     * Converts a list of Merchandise entities to a list of MerchandiseDto objects.
     *
     * @param merchandiseList the list of Merchandise entities to convert
     * @return the corresponding list of MerchandiseDto objects
     */
    List<MerchandiseDto> merchandiseListToMerchandiseDtoList(List<Merchandise> merchandiseList);

    /**
     * Converts a MerchandiseVariant entity to its corresponding MerchandiseVariantDto.
     *
     * @param variant the MerchandiseVariant entity to convert
     * @return the corresponding MerchandiseVariantDto
     */
    MerchandiseVariantDto merchandiseVariantToMerchandiseVariantDto(MerchandiseVariant variant);

    /**
     * Converts a MerchandiseVariantDto to its corresponding MerchandiseVariant entity.
     *
     * @param dto the MerchandiseVariantDto to convert
     * @return the corresponding MerchandiseVariant entity
     */
    MerchandiseVariant merchandiseVariantDtoToMerchandiseVariant(MerchandiseVariantDto dto);

    /**
     * Converts a list of MerchandiseVariant entities to a list of MerchandiseVariantDto objects.
     *
     * @param variants the list of MerchandiseVariant entities to convert
     * @return the corresponding list of MerchandiseVariantDto objects
     */
    List<MerchandiseVariantDto> merchandiseVariantListToMerchandiseVariantDtoList(List<MerchandiseVariant> variants);

}
