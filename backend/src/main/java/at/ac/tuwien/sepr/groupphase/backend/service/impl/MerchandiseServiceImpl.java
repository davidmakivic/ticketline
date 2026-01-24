package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class MerchandiseServiceImpl implements MerchandiseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MerchandiseRepository repository;
    private final MerchandiseMapper mapper;

    public MerchandiseServiceImpl(MerchandiseRepository repository,
                                  MerchandiseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
        LOGGER.debug("MerchandiseServiceImpl initialized");
    }

    @Override
    public List<MerchandiseDto> findAll() {
        LOGGER.debug("Fetching all merchandise items");
        List<Merchandise> entities = repository.findAll();
        LOGGER.debug("Found {} merchandise items", entities.size());
        return mapper.merchandiseListToMerchandiseDtoList(entities);
    }

    @Override
    public MerchandiseDto findById(Long id) {
        LOGGER.debug("Fetching merchandise with id={}", id);
        return repository.findById(id)
            .map(entity -> {
                LOGGER.debug("Merchandise found with id={}", id);
                return mapper.merchandiseToMerchandiseDto(entity);
            })
            .orElseGet(() -> {
                LOGGER.warn("No merchandise found with id={}", id);
                return null;
            });
    }

    @Override
    @Transactional
    public MerchandiseDto save(MerchandiseDto dto) {
        LOGGER.info("Saving merchandise");

        Merchandise entity = mapper.merchandiseDtoToMerchandise(dto);

        if (entity.getVariants() != null) {
            LOGGER.debug("Setting back-reference for {} merchandise variants",
                entity.getVariants().size());
            entity.getVariants().forEach(v -> v.setMerchandise(entity));
        } else {
            LOGGER.debug("No merchandise variants to process");
        }

        Merchandise saved = repository.save(entity);
        LOGGER.info("Merchandise saved with id={}", saved.getId());

        return mapper.merchandiseToMerchandiseDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LOGGER.info("Deleting merchandise with id={}", id);
        repository.deleteById(id);
        LOGGER.debug("Merchandise deletion requested for id={}", id);
    }

    @Override
    public ResponseEntity<byte[]> getMerchandiseImage(Long id) {
        LOGGER.info("Fetching merchandise image for id={}", id);

        Merchandise merchandise = repository.findById(id)
            .orElseThrow(() -> {
                LOGGER.error("Merchandise not found while fetching image, id={}", id);
                return new RuntimeException("Merchandise not found: " + id);
            });

        if (merchandise.getImageData() == null || merchandise.getImageData().length == 0) {
            LOGGER.warn("No image data available for merchandise id={}", id);
            return ResponseEntity.noContent().build();
        }

        LOGGER.debug("Returning image for merchandise id={}, contentType={}, size={} bytes",
            id,
            merchandise.getImageContentType(),
            merchandise.getImageData().length
        );

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(merchandise.getImageContentType()))
            .body(merchandise.getImageData());
    }
}
