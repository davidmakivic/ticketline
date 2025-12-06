package at.ac.tuwien.sepr.groupphase.backend.unittests;



import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PriceCategoryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PriceCategoryMappingTest {

    @Autowired
    private PriceCategoryMapper mapper;

    @Test
    void testEntityToDtoAndBack() {
        PriceCategory entity = new PriceCategory("A", 10.90);

        PriceCategoryDto dto = mapper.priceCategoryToPriceCategoryDto(entity);

        assertNull(dto.getId()); // entity has no id before saving
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getPriceCategory(), dto.getPriceCategory());

        PriceCategory mappedBack = mapper.priceCategoryDtoToPriceCategory(dto);

        assertEquals(entity.getPrice(), mappedBack.getPrice());
        assertEquals(entity.getPriceCategory(), mappedBack.getPriceCategory());
    }

    @Test
    void testListMapping() {
        List<PriceCategory> entities = List.of(
            new PriceCategory("A", 10.90),
            new PriceCategory("B", 15.50)
        );

        List<PriceCategoryDto> dtos = mapper.priceCategoryListToPriceCategoryDtoList(entities);

        assertEquals(2, dtos.size());
        assertEquals("A", dtos.get(0).getPriceCategory());
        assertEquals("B", dtos.get(1).getPriceCategory());
    }
}
