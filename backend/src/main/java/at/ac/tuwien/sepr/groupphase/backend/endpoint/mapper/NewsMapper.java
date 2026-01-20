package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.NewsInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.News;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface NewsMapper {

    @Named("simpleNews")
    @Mapping(source = "imageContentType", target = "imageContentType")
    @Mapping(source = "event", target = "event", qualifiedByName = "eventWithoutPerformances")
    SimpleNewsDto newsToSimpleNewsDto(News news);

    @IterableMapping(qualifiedByName = "simpleNews")
    List<SimpleNewsDto> newsToSimpleNewsDto(List<News> news);

    @Mapping(source = "imageContentType", target = "imageContentType")
    @Mapping(source = "event", target = "event", qualifiedByName = "eventWithoutPerformances")
    DetailedNewsDto newsToDetailedNewsDto(News news);

    @Named("eventWithoutPerformances")
    @Mapping(target = "performances", ignore = true)
    EventDto eventToEventDto(Event event);

    News detailedNewsDtoToNews(DetailedNewsDto detailedNewsDto);

    News newsInquiryDtoToNews(NewsInquiryDto newsInquiryDto);

    NewsInquiryDto newsToNewsInquiryDto(News news);

}

