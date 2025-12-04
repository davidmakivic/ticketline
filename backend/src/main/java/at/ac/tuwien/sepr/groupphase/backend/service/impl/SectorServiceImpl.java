package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SectorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectorServiceImpl implements SectorService {

    private final SectorRepository sectorRepository;
    private final SectorMapper sectorMapper;

    public SectorServiceImpl(SectorRepository sectorRepository, SectorMapper sectorMapper) {
        this.sectorRepository = sectorRepository;
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
        // ID wird von DB generiert
        Sector savedSector = sectorRepository.save(sectorMapper.sectorDtoToSector(sector));
        return sectorMapper.sectorToSectorDto(savedSector);
    }

    @Override
    public SectorDto update(Long id, Sector sector) {
        Sector existing = sectorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sector not found with id: " + id));

        existing.setName(sector.getName());
        existing.setType(sector.getType());
        existing.setPriceCategory(sector.getPriceCategory());
        existing.setHall(sector.getHall());

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
