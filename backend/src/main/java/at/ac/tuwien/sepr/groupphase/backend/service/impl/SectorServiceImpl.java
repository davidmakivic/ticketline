package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SectorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorServiceImpl implements SectorService {

    private final SectorRepository sectorRepository;
    private final HallRepository hallRepository;
    private final SectorMapper sectorMapper;

    public SectorServiceImpl(SectorRepository sectorRepository, HallRepository hallRepository, SectorMapper sectorMapper) {
        this.sectorRepository = sectorRepository;
        this.hallRepository = hallRepository;
        this.sectorMapper = sectorMapper;
    }

    @Override
    public SectorDto findById(Long id) {
        Sector sector =  sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector not found with id: " + id));
        return sectorMapper.sectorToSectorDto(sector);
    }

    @Override
    public List<SectorDto> findAll() {
        List<Sector> allSeats = sectorRepository.findAll();
        return sectorMapper.sectorListToSectorDtoList(allSeats);
    }

    @Override
    public List<SectorDto> findByHallId(Long hallId) {
        return sectorMapper.sectorListToSectorDtoList(sectorRepository.findByHallId(hallId));
    }

    @Override
    public SectorDto create(SectorDto sector) {
        if (sector.getHallId() == null) {
            throw new EntityNotFoundException("Hall with id " + sector.getHallId() + " not found");
        }

        Hall hall = hallRepository.findById(sector.getHallId())
            .orElseThrow(() -> new EntityNotFoundException("Hall with id " + sector.getHallId() + " not found"));

        Sector entity = sectorMapper.sectorDtoToSector(sector);
        entity.setHall(hall);

        Sector savedSector = sectorRepository.save(entity);
        return sectorMapper.sectorToSectorDto(savedSector);
    }

    @Override
    public SectorDto update(Long id, SectorDto sector) {
        Sector existing = sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector not found with id: " + id));

        if (sector.getName() != null) {
            existing.setName(sector.getName());
        }

        if (sector.getType() != null) {
            existing.setType(sector.getType());
        }

        if (sector.getPriceCategory() != null) {
            existing.setPriceCategory(sector.getPriceCategory());
        }

        if (sector.getHallId() != null) {
            Hall hall =  hallRepository.findById(sector.getHallId())
                .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found"));

            existing.setHall(hall);
        }

        Sector updatedSector = sectorRepository.save(existing);
        return sectorMapper.sectorToSectorDto(updatedSector);
    }

    @Override
    public void delete(Long id) {
        Sector sector = sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector not found with id: " + id));
        sectorRepository.delete(sector);
    }

}
