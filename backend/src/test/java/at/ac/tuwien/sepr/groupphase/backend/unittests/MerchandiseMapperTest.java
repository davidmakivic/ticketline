package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MerchandiseMapperTest {

    private final MerchandiseMapper merchandiseMapper =
        Mappers.getMapper(MerchandiseMapper.class);

    private Merchandise createMerchandise() {
        Merchandise merchandise = new Merchandise();
        merchandise.setId(1L);
        merchandise.setName("T-Shirt");
        merchandise.setDescription("Black band T-Shirt");
        merchandise.setPrice(2500);
        merchandise.setQuantity(10);
        return merchandise;
    }

    @Test
    void merchandiseToMerchandiseDto_shouldMapCorrectly() {
        Merchandise merchandise = createMerchandise();

        MerchandiseDto dto = merchandiseMapper.merchandiseToMerchandiseDto(merchandise);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("T-Shirt", dto.getName());
        assertEquals("Black band T-Shirt", dto.getDescription());
        assertEquals(2500, dto.getPrice());
        assertEquals(10, dto.getQuantity());
    }

    @Test
    void merchandiseListToMerchandiseDtoList_shouldMapListCorrectly() {
        Merchandise m1 = createMerchandise();
        Merchandise m2 = createMerchandise();
        m2.setId(2L);
        m2.setName("Hoodie");

        List<MerchandiseDto> dtos =
            merchandiseMapper.merchandiseListToMerchandiseDtoList(List.of(m1, m2));

        assertEquals(2, dtos.size());
        assertEquals("T-Shirt", dtos.get(0).getName());
        assertEquals("Hoodie", dtos.get(1).getName());
    }
}
