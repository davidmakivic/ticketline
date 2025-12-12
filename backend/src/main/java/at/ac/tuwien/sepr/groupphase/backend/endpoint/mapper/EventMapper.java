package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PerformanceMapper.class})
public interface EventMapper {

    @Mapping(source = "imageContentType", target = "imageContentType")
    EventDto eventToEventDto(Event event);

    @Mapping(source = "imageContentType", target = "imageContentType")
    @Mapping(target = "artists", ignore = true)
    List<EventDto> eventToEventDto(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    @Mapping(target = "imageContentType", ignore = true)
    @Mapping(target = "artists", ignore = true)
    Event eventDtoToEvent(EventDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageData", ignore = true)
    @Mapping(target = "imageContentType", ignore = true)
    @Mapping(target = "artists", ignore = true)
    void updateEntityFromDto(EventDto dto, @MappingTarget Event entity);
}
