package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.HallUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.HallMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.HallService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HallServiceImpl implements HallService {

    private HallRepository hallRepository;
    private VenueRepository venueRepository;
    private HallMapper hallMapper;

    public HallServiceImpl(HallRepository hallRepository, VenueRepository venueRepository, HallMapper hallMapper) {
        this.hallRepository = hallRepository;
        this.venueRepository = venueRepository;
        this.hallMapper = hallMapper;
    }

    @Override
    public HallDto createHall(HallCreateDto dto) {
        Venue venue = venueRepository.findById(dto.getVenueId())
            .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + dto.getVenueId() + " not found"));

        Hall hall = new Hall();
        hall.setVenue(venue);
        hall.setName(dto.getName());
        hall.setLayoutMetadata(dto.getLayoutMetadata());

        hallRepository.save(hall);
        return hallMapper.hallToHallDto(hall);
    }

    @Override
    public HallDto updateHall(Long id, HallUpdateDto dto) {
        Hall hall = hallRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Hall with id: " + id + " not found"));

        if (dto.getVenueId() != null) {
            Venue venue = venueRepository.findById(dto.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found"));
            hall.setVenue(venue);
        }

        if (dto.getName() != null) {
            hall.setName(dto.getName());
        }

        if (dto.getLayoutMetadata() != null) {
            hall.setLayoutMetadata(dto.getLayoutMetadata());
        }

        hallRepository.save(hall);
        return hallMapper.hallToHallDto(hall);
    }

    @Override
    public void deleteHall(Long id) {
        if (!hallRepository.existsById(id)) {
            throw new EntityNotFoundException("Hall with id: " + id + " not found");
        }

        hallRepository.deleteById(id);
    }

    @Override
    public HallDto getHallbyId(Long id) {
        Hall hall = hallRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Hall with id: " + id + " not found"));

        return hallMapper.hallToHallDto(hall);
    }

    @Override
    public List<HallDto> findAll() {
        return hallMapper.hallListToHallDtoList(hallRepository.findAll());
    }
}
