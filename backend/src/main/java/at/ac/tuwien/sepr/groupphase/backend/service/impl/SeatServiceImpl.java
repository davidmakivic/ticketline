package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SeatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SeatService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {


    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final SectorRepository sectorRepository;

    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper, SectorRepository sectorRepository) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
        this.sectorRepository = sectorRepository;
    }

    @Override
    public SeatDto findById(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Seat not found with id: " + id));
        return seatMapper.seatToSeatDto(seat);
    }

    @Override
    public List<SeatDto> findAll() {
        List<Seat> allSeats = seatRepository.findAll();
        return seatMapper.seatListToSeatDtoList(allSeats);
    }

    @Override
    public List<SeatDto> findBySectorId(Long sectorId) {
        return seatMapper.seatListToSeatDtoList(seatRepository.findBySectorId(sectorId));
    }

    @Override
    public SeatDto create(SeatCreateDto seat) {
        if (seat.getSectorId() == null) {
            throw new NotFoundException("Sector with id " + seat.getSectorId() + " not found");
        }
        Sector sector = sectorRepository.findById(seat.getSectorId())
            .orElseThrow(() -> new NotFoundException("Sector with id " + seat.getSectorId() + " not found"));

        Seat entity = seatMapper.seatCreateDtoToSeat(seat);
        entity.setSector(sector);

        Seat savedSeat = seatRepository.save(entity);
        return seatMapper.seatToSeatDto(savedSeat);
    }

    @Override
    public SeatDto update(Long id, SeatUpdateDto seat) {
        Seat existingSeat = seatRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Seat not found with id: " + id));

        Sector sector = sectorRepository.findById(seat.getSectorId())
            .orElseThrow(() -> new NotFoundException("Sector not found with id: " + seat.getSectorId()));

        // Felder updaten
        existingSeat.setRowNumber(seat.getRowNumber());
        existingSeat.setSeatNumber(seat.getSeatNumber());
        existingSeat.setSector(sector);

        Seat savedSeat = seatRepository.save(existingSeat);
        return seatMapper.seatToSeatDto(savedSeat);
    }

    @Override
    public void delete(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Seat not found with id: " + id));
        seatRepository.delete(seat);
    }
}
