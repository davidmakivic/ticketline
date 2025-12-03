package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ArtistService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist create(Artist artist) {
        return artistRepository.save(artist);
    }

    @Override
    public Artist update(Long id, Artist artist) {
        Artist existing = artistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Artist not found: " + id));

        existing.setFirstName(artist.getFirstName());
        existing.setLastName(artist.getLastName());
        existing.setStageName(artist.getStageName());
        existing.setArtistType(artist.getArtistType());

        return artistRepository.save(existing);
    }

    @Override
    public Artist findById(Long id) {
        return artistRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Artist not found with id" + id));
    }

    @Override
    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    @Override
    public List<Artist> findByName(String name) {
        return artistRepository.findByAnyName(name);
    }

    @Override
    public void delete(Long id) {
        artistRepository.deleteById(id);
    }
}
