package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallUpdateDto;

import java.util.List;

public interface HallService {

    HallDto createHall(HallCreateDto dto);

    HallDto updateHall(Long id, HallUpdateDto dto);

    void deleteHall(Long id);

    HallDto getHallbyId(Long id);

    List<HallDto> findAll();
}
