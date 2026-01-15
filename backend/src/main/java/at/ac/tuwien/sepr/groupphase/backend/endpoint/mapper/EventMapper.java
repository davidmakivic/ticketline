package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PerformanceMapper.class})
public interface EventMapper {
    @Named("withPerformances")
    @Mapping(source = "imageContentType", target = "imageContentType")
    EventDto eventToEventDtoWithPerformances(Event event);

    @Named("withoutPerformances")
    @Mapping(source = "imageContentType", target = "imageContentType")
    @Mapping(target = "performances", ignore = true)
    EventDto eventToEventDto(Event event);

    @Mapping(source = "imageContentType", target = "imageContentType")
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "performances", ignore = true)
    List<EventDto> eventToEventDtoList(List<Event> events);

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



    default Page<EventDto> eventPageToEventDtoPage(Page<Event> events) {
        return events.map(this::eventToEventDto);
    }
}
