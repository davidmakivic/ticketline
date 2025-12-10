package at.ac.tuwien.sepr.groupphase.backend.integrationtest;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepr.groupphase.backend.service.EventService;
import at.ac.tuwien.sepr.groupphase.backend.type.ArtistType;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class ArtistServiceTest {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventService eventService;

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Transactional
    @Test
    void testCreateArtist() throws IOException {
        ArtistDto saved = artistService.create("Test", "TestLastName", "TestStage", ArtistType.SOLO, null);

        assertThat(saved.getStageName()).isEqualTo("TestStage");
    }

    @Transactional
    @Test
    void testUpdateArtist() throws IOException {
        ArtistDto saved = artistService.create("Test", "TestLastName", "TestStage", ArtistType.SOLO, null);
        Long id = saved.getId();

        ArtistDto result = artistService.update(id, "Updated", "UpdatedLastName", "UpdatedStage", ArtistType.SOLO, null);

        assertThat(result.getStageName()).isEqualTo("UpdatedStage");
        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("UpdatedLastName");
    }

    @Transactional
    @Test
    void testUpdateArtistNotFound() {
        assertThatThrownBy(() -> artistService.update(999L, "Test", "Last", "Stage", ArtistType.SOLO, null))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testFindAllArtists() throws IOException {
        artistService.create("Test", "TestLastName", "TestStage", ArtistType.SOLO, null);
        artistService.create("Test2", "TestLastName2", "TestStage2", ArtistType.BAND, null);

        List<ArtistDto> result = artistService.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void testDeleteArtist() throws IOException {
        ArtistDto saved = artistService.create("Test", "TestLastName", "TestStage", ArtistType.SOLO, null);
        Long id = saved.getId();

        assertThat(artistService.findById(id)).isNotNull();

        artistService.delete(id);

        assertThatThrownBy(() -> artistService.findById(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Transactional
    @Test
    void testAddEventToArtist() throws IOException {
        ArtistDto savedArtist = artistService.create("A", "B", "TestStage", ArtistType.SOLO, null);
        EventDto savedEvent = eventService.create("My Event", "desc", EventType.CONCERT, 90, null);

        artistService.addEvent(savedArtist.getId(), savedEvent.getId());

        ArtistDto updated = artistService.findById(savedArtist.getId());
        assertThat(updated.getEvents()).hasSize(1);
    }

    @Transactional
    @Test
    void testRemoveEventFromArtist() throws IOException {
        ArtistDto savedArtist = artistService.create("A", "B", "TestStage", ArtistType.SOLO, null);
        EventDto savedEvent = eventService.create("My Event", "desc", EventType.MUSICAL, 90, null);

        artistService.addEvent(savedArtist.getId(), savedEvent.getId());
        artistService.deleteEvent(savedArtist.getId(), savedEvent.getId());

        ArtistDto updated = artistService.findById(savedArtist.getId());
        assertThat(updated.getEvents()).isEmpty();
    }

    @Transactional
    @Test
    void testFindByNamePositive() throws IOException {
        artistService.create("David", "Bowie", "Ziggy", ArtistType.SOLO, null);
        artistService.create("John", "Lennon", "Beatles", ArtistType.BAND, null);

        List<ArtistDto> result = artistService.findByName("ziggy");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStageName()).isEqualTo("Ziggy");
    }

    @Transactional
    @Test
    void testFindByNameNegative() throws IOException {
        artistService.create("Test", "Artist", "TestStage", ArtistType.SOLO, null);

        List<ArtistDto> result = artistService.findByName("nonexistent");

        assertThat(result).isEmpty();
    }
}
