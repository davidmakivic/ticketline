package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SeatMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SeatMapperTest {

    @Autowired
    private SeatMapper seatMapper;

    @Test
    void testSeatToSeatDtoAndBack() {
        Sector sector = new Sector();
        Seat seat = new Seat(1, 2, sector); // row, number, sectorId
        seat.setId(1L);
        SeatDto dto = seatMapper.seatToSeatDto(seat);
        assertEquals(seat.getId(), dto.getId());
        assertEquals(seat.getRowNumber(), dto.getRowNumber());
        assertEquals(seat.getSeatNumber(), dto.getSeatNumber());
        assertEquals(seat.getSector().getId(), dto.getSectorId());

        Seat mappedBack = seatMapper.seatDtoToSeat(dto);
        assertEquals(seat.getId(), mappedBack.getId());
    }

    @Test
    void testSeatListMapping() {
        Sector sector = new Sector();
        List<Seat> seats = List.of(new Seat(1,1,sector), new Seat(2,2,sector));
        List<SeatDto> dtos = seatMapper.seatListToSeatDtoList(seats);
        assertEquals(2, dtos.size());
    }
}
