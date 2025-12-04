package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
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
        Sector sector = new Sector(1L, "A", SectorType.SEATED, "B");
        SectorDto dto = sectorMapper.sectorToSectorDto(sector);
        assertEquals(sector.getId(), dto.getId());
        assertEquals(sector.getHallId(), dto.getHallId());

        Sector mappedBack = sectorMapper.sectorDtoToSector(dto);
        assertEquals(sector.getName(), mappedBack.getName());
    }

    @Test
    void testSectorListMapping() {
        List<Sector> sectors = List.of(
            new Sector(1L, "A", SectorType.SEATED, "B"),
            new Sector(1L, "B", SectorType.SEATED, "C")
        );
        List<SectorDto> dtos = sectorMapper.sectorListToSectorDtoList(sectors);
        assertEquals(2, dtos.size());
    }

}
