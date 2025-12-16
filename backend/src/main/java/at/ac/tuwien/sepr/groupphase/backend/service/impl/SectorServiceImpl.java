package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SectorUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.HallRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SectorServiceImpl implements SectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SectorRepository sectorRepository;
    private final HallRepository hallRepository;
    private final SectorMapper sectorMapper;
    private final PriceCategoryRepository priceCategoryRepository;

    public SectorServiceImpl(SectorRepository sectorRepository, HallRepository hallRepository, SectorMapper sectorMapper, PriceCategoryRepository priceCategoryRepository) {
        this.sectorRepository = sectorRepository;
        this.hallRepository = hallRepository;
        this.sectorMapper = sectorMapper;
        this.priceCategoryRepository = priceCategoryRepository;
    }

    @Override
    public SectorDto findById(Long id) {
        LOGGER.info("Fetching sector with id={}", id);
        Sector sector =  sectorRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Sector not found with id: " + id));
        return sectorMapper.sectorToSectorDto(sector);
    }

    @Override
    public List<SectorDto> findAll() {
        LOGGER.info("Fetching all sectors");
        List<Sector> allSeats = sectorRepository.findAll();
        return sectorMapper.sectorListToSectorDtoList(allSeats);
    }

    @Override
    public List<SectorDto> findByHallId(Long hallId) {
        LOGGER.info("Fetching sectors for hall {}", hallId);
        return sectorMapper.sectorListToSectorDtoList(sectorRepository.findByHallId(hallId));
    }

    @Override
    public SectorDto create(SectorCreateDto sector) {
        LOGGER.info("Creating sector in hall {}", sector.getHallId());
        LOGGER.debug("Payload: {}", sector);
        if (sector.getHallId() == null) {
            throw new NotFoundException("Hall with id " + sector.getHallId() + " not found");
        }

        Hall hall = hallRepository.findById(sector.getHallId())
            .orElseThrow(() ->
                new NotFoundException("Hall with id " + sector.getHallId() + " not found"));

        PriceCategory priceCategory = priceCategoryRepository.findById(sector.getPriceCategoryId())
            .orElseThrow(() ->
                new NotFoundException("PriceCategory with id " + sector.getPriceCategoryId() + " not found"));

        Sector entity = sectorMapper.sectorCreateDtoToSector(sector);
        entity.setHall(hall);
        entity.setPriceCategory(priceCategory);

        Sector savedSector = sectorRepository.save(entity);
        return sectorMapper.sectorToSectorDto(savedSector);
    }

    @Override
    public SectorDto update(Long id, SectorUpdateDto sector) {
        LOGGER.info("Updating sector with id={}", id);
        LOGGER.debug("Payload: {}", sector);
        Sector existing = sectorRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Sector not found with id: " + id));

        if (sector.getName() != null) {
            existing.setName(sector.getName());
        }

        if (sector.getType() != null) {
            existing.setType(sector.getType());
        }

        if (sector.getPriceCategoryId() != null) {
            PriceCategory priceCategory =  priceCategoryRepository.findById(sector.getPriceCategoryId())
                .orElseThrow(() -> new NotFoundException("PriceCategory with id: " + id + " not found"));

            existing.setPriceCategory(priceCategory);
        }

        if (sector.getHallId() != null) {
            Hall hall =  hallRepository.findById(sector.getHallId())
                .orElseThrow(() -> new NotFoundException("Venue with id: " + id + " not found"));

            existing.setHall(hall);
        }

        Sector updatedSector = sectorRepository.save(existing);
        return sectorMapper.sectorToSectorDto(updatedSector);
    }

    @Override
    public void delete(Long id) {
        LOGGER.info("Deleting sector with id={}", id);
        Sector sector = sectorRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Sector not found with id: " + id));
        sectorRepository.delete(sector);
    }

}
