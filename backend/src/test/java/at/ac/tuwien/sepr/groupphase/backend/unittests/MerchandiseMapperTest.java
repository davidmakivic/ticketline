package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.entity.MerchandiseVariant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MerchandiseMapperTest {

    private final MerchandiseMapper merchandiseMapper = Mappers.getMapper(MerchandiseMapper.class);

    private Merchandise createMerchandise() {
        Merchandise merchandise = new Merchandise();
        merchandise.setId(1L);
        merchandise.setName("T-Shirt");
        merchandise.setDescription("Black band T-Shirt");
        merchandise.setPrice(2500);

        // Variante hinzufügen
        MerchandiseVariant variant = new MerchandiseVariant();
        variant.setSize("M");
        variant.setQuantity(10);
        variant.setMerchandise(merchandise);
        merchandise.getVariants().add(variant);

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
        assertEquals(1, dto.getVariants().size());
        assertEquals("M", dto.getVariants().get(0).getSize());
        assertEquals(10, dto.getVariants().get(0).getQuantity());
    }

    @Test
    void merchandiseListToMerchandiseDtoList_shouldMapListCorrectly() {
        Merchandise m1 = createMerchandise();
        Merchandise m2 = createMerchandise();
        m2.setId(2L);
        m2.setName("Hoodie");
        m2.getVariants().clear();
        m2.getVariants().add(new MerchandiseVariant(){{
            setSize("L");
            setQuantity(5);
            setMerchandise(m2);
        }});

        List<MerchandiseDto> dtos = merchandiseMapper.merchandiseListToMerchandiseDtoList(List.of(m1, m2));

        assertEquals(2, dtos.size());
        assertEquals("T-Shirt", dtos.get(0).getName());
        assertEquals(1, dtos.get(0).getVariants().size());
        assertEquals("Hoodie", dtos.get(1).getName());
        assertEquals(1, dtos.get(1).getVariants().size());
        assertEquals("L", dtos.get(1).getVariants().get(0).getSize());
    }
}
