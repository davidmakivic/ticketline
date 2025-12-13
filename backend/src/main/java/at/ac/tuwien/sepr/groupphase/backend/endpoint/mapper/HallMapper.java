package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {VenueMapper.class})
public interface HallMapper {

    /**
     * Converts a Hall entity to its corresponding HallDto.
     *
     * @param hall the Hall entity to be converted
     * @return the corresponding HallDto
     */
    @Mapping(target = "venueId", source = "venue.id")
    HallDto hallToHallDto(Hall hall);

    /**
     * Converts a Hall Dto to its corresponding Hall entity.
     *
     * @param hallDto the HallDto to be converted
     * @return the corresponding Hall entity
     */
    @Mapping(target = "venue", ignore = true)
    Hall hallDtoToHall(HallDto hallDto);


    /**
     * Converts a list of Hall entities to a list of HallDto objects.
     *
     * @param halls the list of Hall entities to convert
     * @return the corresponding list of HallDto objects
     */
    List<HallDto> hallListToHallDtoList(List<Hall> halls);
}
