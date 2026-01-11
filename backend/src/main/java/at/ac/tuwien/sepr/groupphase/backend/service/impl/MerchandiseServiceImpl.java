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

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class MerchandiseServiceImpl implements MerchandiseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MerchandiseRepository repository;
    private final MerchandiseMapper mapper;

    public MerchandiseServiceImpl(MerchandiseRepository repository, MerchandiseMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<MerchandiseDto> findAll() {
        return mapper.merchandiseListToMerchandiseDtoList(repository.findAll());
    }

    @Override
    public MerchandiseDto findById(Long id) {
        return repository.findById(id)
            .map(mapper::merchandiseToMerchandiseDto)
            .orElse(null);
    }

    @Override
    public MerchandiseDto save(MerchandiseDto merchandiseDto) {
        Merchandise entity = mapper.merchandiseDtoToMerchandise(merchandiseDto);
        Merchandise saved = repository.save(entity);
        return mapper.merchandiseToMerchandiseDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public ResponseEntity<byte[]> getMerchandiseImage(Long id) {
        LOGGER.info("Fetching merchandise image for id={}", id);
        Merchandise merchandise = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Merchandise not found: " + id));

        if (merchandise.getImageData() == null || merchandise.getImageData().length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(merchandise.getImageContentType()))
            .body(merchandise.getImageData());
    }

}
