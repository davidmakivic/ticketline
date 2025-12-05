package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    VenueDto venuetoVenueDto(Venue venue);

    Venue venueDtoToVenue(VenueDto venueDto);

    List<VenueDto> venueListToVenueDtoList(List<Venue> venue);
}
