package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.type.SectorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SectorMapperTest {

    @Autowired
    private SectorMapper sectorMapper;

    @Test
    void testSectorToSectorDtoAndBack() {
        Hall hall = new Hall();
        Sector sector = new Sector(hall, "A", SectorType.SEATED, "B");
        SectorDto dto = sectorMapper.sectorToSectorDto(sector);
        assertEquals(sector.getId(), dto.getId());
        assertEquals(sector.getHall().getId(), dto.getHallId());

        Sector mappedBack = sectorMapper.sectorDtoToSector(dto);
        assertEquals(sector.getName(), mappedBack.getName());
    }

    @Test
    void testSectorListMapping() {
        Hall hall1= new Hall();
        Hall hall2 = new Hall();
        List<Sector> sectors = List.of(
            new Sector(hall1, "A", SectorType.SEATED, "B"),
            new Sector(hall2, "B", SectorType.SEATED, "C")
        );
        List<SectorDto> dtos = sectorMapper.sectorListToSectorDtoList(sectors);
        assertEquals(2, dtos.size());
    }

}
