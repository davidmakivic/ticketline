package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ArtistSearchTest {

    @Autowired
    private ArtistRepository artistRepository;


    @Test
    public void artistSearchTest(){
        Artist custom1 = new Artist();
        custom1.setFirstName("Freddie");
        custom1.setLastName("Mercury");
        custom1.setStageName("Queen");
        custom1.setArtistType(ArtistType.BAND);
        artistRepository.save(custom1);

        Artist custom2 = new Artist();
        custom2.setFirstName("Elvis");
        custom2.setLastName("Presley");
        custom2.setStageName("The King");
        custom2.setArtistType(ArtistType.SOLO);
        artistRepository.save(custom2);

        List<Artist> result = artistRepository.findByAnyName("qu");
        assert(result.size() == 1);
        assert(result.getFirst().getStageName().equals("Queen"));
    }

}
