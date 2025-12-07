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
        PriceCategory priceCategoryEntity = new PriceCategory();
        priceCategoryEntity.setName("B");
        priceCategoryEntity.setPrice(10.90);

        PriceCategoryDto dto = mapper.priceCategoryToPriceCategoryDto(priceCategoryEntity);

        assertNull(dto.getId()); // entity has no id before saving
        assertEquals(priceCategoryEntity.getPrice(), dto.getPrice());
        assertEquals(priceCategoryEntity.getName(), dto.getName());

        PriceCategory mappedBack = mapper.priceCategoryDtoToPriceCategory(dto);

        assertEquals(priceCategoryEntity.getPrice(), mappedBack.getPrice());
        assertEquals(priceCategoryEntity.getName(), mappedBack.getName());
    }

    @Test
    void testListMapping() {

        PriceCategory priceCategoryEntityA = new PriceCategory();
        priceCategoryEntityA.setName("A");
        priceCategoryEntityA.setPrice(10.90);

        PriceCategory priceCategoryEntityB = new PriceCategory();
        priceCategoryEntityB.setName("B");
        priceCategoryEntityB.setPrice(15.50);

        List<PriceCategory> entities = List.of(
            priceCategoryEntityA, priceCategoryEntityB
        );

        List<PriceCategoryDto> dtos = mapper.priceCategoryListToPriceCategoryDtoList(entities);

        assertEquals(2, dtos.size());
        assertEquals("A", dtos.get(0).getName());
        assertEquals("B", dtos.get(1).getName());
    }
}
