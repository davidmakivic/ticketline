package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HallMapper {
    HallDto hallToHallDto(Hall hall);

    Hall hallDtoToHall(HallDto hallDto);

    List<HallDto> hallListToHallDtoList(List<Hall> halls);
}
