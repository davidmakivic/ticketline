package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(
        target = "ticketIds",
        expression = "java(reservation.getTickets() == null ? java.util.Collections.emptyList() : reservation.getTickets().stream().map(t -> t.getId()).toList())"
    )
    ReservationDto reservationToReservationDto(Reservation reservation);

    List<ReservationDto> reservationListToReservationDtoList(List<Reservation> reservations);
}
