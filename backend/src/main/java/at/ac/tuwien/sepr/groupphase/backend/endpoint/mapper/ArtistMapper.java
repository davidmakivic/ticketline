package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @Mapping(source = "imageContentType", target = "imageContentType")
    ArtistDto artistToArtistDto(Artist artist);

    @Mapping(source = "imageContentType", target = "imageContentType")
    List<ArtistDto> artistToArtistDto(List<Artist> artists);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    @Mapping(target = "imageContentType", ignore = true)
    @Mapping(target = "events", ignore = true)
    Artist artistDtoToArtist(ArtistDto artistDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    @Mapping(target = "imageContentType", ignore = true)
    @Mapping(target = "events", ignore = true)
    void updateEntityFromDto(ArtistDto dto, @MappingTarget Artist entity);
}
