package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Mapping(source = "event.id",  target = "eventId")
    @Mapping(source = "hall.id",   target = "hallId")
    PerformanceDto performanceToPerformanceDto(Performance performance);

    @Mapping(target = "id",    ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "hall",  ignore = true)
    Performance performanceDtoToPerformance(PerformanceDto dto);

    List<PerformanceDto> performanceListToPerformanceDtoList(List<Performance> list);
}
