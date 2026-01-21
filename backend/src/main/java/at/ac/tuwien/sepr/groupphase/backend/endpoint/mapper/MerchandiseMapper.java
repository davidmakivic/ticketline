package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseVariantDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MerchandiseMapper {

    MerchandiseDto merchandiseToMerchandiseDto(Merchandise merchandise);

    Merchandise merchandiseDtoToMerchandise(MerchandiseDto merchandiseDto);

    List<MerchandiseDto> merchandiseListToMerchandiseDtoList(List<Merchandise> merchandiseList);

    MerchandiseVariantDto merchandiseVariantToMerchandiseVariantDto(MerchandiseVariant variant);

    MerchandiseVariant merchandiseVariantDtoToMerchandiseVariant(MerchandiseVariantDto dto);

    List<MerchandiseVariantDto> merchandiseVariantListToMerchandiseVariantDtoList(List<MerchandiseVariant> variants);
}
