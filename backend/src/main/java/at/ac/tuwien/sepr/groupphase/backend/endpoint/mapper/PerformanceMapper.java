package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {
    PerformanceDto performanceToPerformanceDto(Performance performance);

    @Mapping(target = "id",  ignore = true)
    Performance performanceDtoToPerformance(Performance dto);


}
