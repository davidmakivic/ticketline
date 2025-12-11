package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectorMapper {

    /**
     * Converts a Sector entity to its corresponding SectorDto.
     *
     * @param sector the Sector entity to convert
     * @return the corresponding SectorDto
     */
    @Mapping(target = "hallId", source = "hall.id")
    @Mapping(target = "priceCategoryId", source = "priceCategory.id")
    SectorDto sectorToSectorDto(Sector sector);


    /**
     * Converts a SectorDto to its corresponding Sector entity.
     *
     * @param sectorDto the SectorDto to convert
     * @return the corresponding Sector entity
     */
    @Mapping(target = "hall", ignore = true)
    @Mapping(target = "priceCategory", ignore = true)
    Sector sectorDtoToSector(SectorDto sectorDto);


    /**
     * Converts a SectorCreateDto to its corresponding Sector entity.
     *
     * @param sectorCreateDto the SectorCreateDto to convert
     * @return the corresponding Sector entity
     */
    @Mapping(target = "hall", ignore = true)
    @Mapping(target = "priceCategory", ignore = true)
    Sector sectorCreateDtoToSector(SectorCreateDto sectorCreateDto);


    /**
     * Converts a SectorUpdateDto to its corresponding Sector entity.
     *
     * @param sectorUpdateDto the SectorUpdateDto to convert
     * @return the corresponding Sector entity
     */
    @Mapping(target = "hall", ignore = true)
    @Mapping(target = "priceCategory", ignore = true)
    Sector sectorUpdateDtoToSector(SectorUpdateDto sectorUpdateDto);

    /**
     * Converts a list of Sector entities to a list of SectorDto objects.
     *
     * @param sectors the list of Sector entities to convert
     * @return the corresponding list of SectorDto objects
     */
    List<SectorDto> sectorListToSectorDtoList(List<Sector> sectors);
}
