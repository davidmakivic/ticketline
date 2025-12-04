package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.VenueDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.VenueMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.VenueRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.VenueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {
    private final VenueMapper venueMapper;
    private final VenueRepository venueRepository;

    public VenueServiceImpl(VenueMapper venueMapper,  VenueRepository venueRepository) {
        this.venueMapper = venueMapper;
        this.venueRepository  = venueRepository;
    }

    @Override
    public VenueDto create(VenueDto venue) {
        Venue saved = venueMapper.venueDtoToVenue(venue);
        venueRepository.save(saved);
        return venueMapper.venuetoVenueDto(saved);
    }

    @Override
    public VenueDto findById(Long id) {
        Venue venue  = venueRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Venue with id: " + id + " not found"));

        return venueMapper.venuetoVenueDto(venue);
    }

    @Override
    public List<VenueDto> findAll() {
        return venueMapper.venueListToTicketDtoList(venueRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new EntityNotFoundException("Venue with id: " + id + " not found");
        }
        venueRepository.deleteById(id);
    }

    @Override
    public VenueDto update(Long id, VenueDto updatedVenue) {
        Venue existing = venueRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Venue with id: " + id + " not found"));

        existing.setName(updatedVenue.getName());
        existing.setStreet(updatedVenue.getStreet());
        existing.setCity(updatedVenue.getCity());
        existing.setCountry(updatedVenue.getCountry());
        existing.setPostalCode(updatedVenue.getPostalCode());

        venueRepository.save(existing);

        return venueMapper.venuetoVenueDto(existing);
    }
}
