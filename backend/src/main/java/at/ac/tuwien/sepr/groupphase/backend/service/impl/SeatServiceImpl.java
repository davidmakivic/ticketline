package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SeatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SeatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {


    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    @Override
    public SeatDto findById(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));
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
    public SeatDto create(Seat seat) {
        // ID wird von DB generiert
        Seat savedSeat = seatRepository.save(seat);
        return seatMapper.seatToSeatDto(savedSeat);
    }

    @Override
    public SeatDto update(Long id, SeatDto seat) {
        Seat existingSeat = seatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));

        // Felder updaten
        existingSeat.setRowNumber(seat.getRowNumber());
        existingSeat.setSeatNumber(seat.getSeatNumber());
        existingSeat.setSectorId(seat.getSectorId());

        Seat savedSeat = seatRepository.save(existingSeat);
        return seatMapper.seatToSeatDto(savedSeat);
    }

    @Override
    public void delete(Long id) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Seat not found with id: " + id));
        seatRepository.delete(seat);
    }
}
