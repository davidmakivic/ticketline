package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;

import java.util.List;

public interface ArtistService {
    Artist create(Artist artist);

    Artist update(Long id, Artist artist);

    Artist findById(Long id);

    List<Artist> findAll();

    List<Artist> findByName(String name);

    void delete(Long id);
}
