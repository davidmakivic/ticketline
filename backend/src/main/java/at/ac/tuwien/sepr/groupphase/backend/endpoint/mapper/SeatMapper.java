package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    /**
     * Converts a Seat entity to its corresponding SeatDto.
     *
     * @param seat the Seat entity to convert
     * @return the corresponding SeatDto
     */
    @Mapping(target = "sectorId", source = "sector.id")
    SeatDto seatToSeatDto(Seat seat);

    /**
     * Converts a SeatDto to its corresponding Seat entity.
     *
     * @param seatDto the SeatDto to convert
     * @return the corresponding Seat entity
     */
    @Mapping(target = "sector", ignore = true)
    Seat seatDtoToSeat(SeatDto seatDto);

    /**
     * Converts a list of Seat entities to a list of SeatDto objects.
     *
     * @param seats the list of Seat entities to convert
     * @return the corresponding list of SeatDto objects
     */
    List<SeatDto> seatListToSeatDtoList(List<Seat> seats);

    /**
     * Converts a SeatCreateDto to its corresponding Seat entity.
     *
     * @param seatCreateDto the SeatCreateDto to convert
     * @return the corresponding Seat entity
     */
    @Mapping(target = "sector", ignore = true)
    Seat seatCreateDtoToSeat(SeatCreateDto seatCreateDto);

    /**
     * Converts a SeatUpdateDto to its corresponding Seat entity.
     *
     * @param seatUpdateDto the SeatUpdateDto to convert
     * @return the corresponding Seat entity
     */
    @Mapping(target = "sector", ignore = true)
    Seat seatUpdateDtoToSeat(SeatUpdateDto seatUpdateDto);
}
