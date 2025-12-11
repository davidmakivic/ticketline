package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
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
        PriceCategory priceCategory = new PriceCategory();
        Sector sector = new Sector(hall, "A", SectorType.SEATED, priceCategory, "1");
        sector.setId(1L);
        SectorDto dto = sectorMapper.sectorToSectorDto(sector);
        assertEquals(sector.getId(), dto.getId());
        assertEquals(sector.getHall().getId(), dto.getHallId());
        assertEquals(sector.getPriceCategory().getId(), dto.getPriceCategoryId());

        Sector mappedBack = sectorMapper.sectorDtoToSector(dto);
        assertEquals(sector.getName(), mappedBack.getName());
    }

    @Test
    void testSectorListMapping() {
        Hall hall1= new Hall();
        Hall hall2 = new Hall();
        PriceCategory priceCategory1 = new PriceCategory();
        PriceCategory priceCategory2 = new PriceCategory();
        List<Sector> sectors = List.of(
            new Sector(hall1, "A", SectorType.SEATED, priceCategory1, "2"),
            new Sector(hall2, "B", SectorType.SEATED, priceCategory2, "3")
        );
        List<SectorDto> dtos = sectorMapper.sectorListToSectorDtoList(sectors);
        assertEquals(2, dtos.size());
    }

}
