package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PriceCategoryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PriceCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class PriceCategoryServiceImpl implements PriceCategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PriceCategoryRepository repository;
    private final PriceCategoryMapper mapper;

    public PriceCategoryServiceImpl(PriceCategoryRepository repository, PriceCategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<PriceCategoryDto> findAll() {
        LOGGER.info("Fetching all price categories");
        List<PriceCategory> entities = repository.findAll();
        return mapper.priceCategoryListToPriceCategoryDtoList(entities);
    }

    @Override
    public PriceCategoryDto findById(Long id) {
        LOGGER.info("Fetching price category with id={}", id);
        return repository.findById(id)
            .map(mapper::priceCategoryToPriceCategoryDto)
            .orElse(null);
    }

    @Override
    public PriceCategoryDto update(Long id, PriceCategoryUpdateDto dto) {
        LOGGER.info("Updating price category with id={}", id);
        LOGGER.debug("Payload: {}", dto);

        PriceCategory existingPriceCategory = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("PriceCategory not found with id: " + id));


        // Felder updaten
        existingPriceCategory.setName(dto.getName());
        existingPriceCategory.setPrice(dto.getPrice());


        PriceCategory savedPriceCategory = repository.save(existingPriceCategory);
        return mapper.priceCategoryToPriceCategoryDto(savedPriceCategory);
    }


    @Override
    public PriceCategoryDto create(PriceCategoryCreateDto dto) {
        LOGGER.info("Creating price category");
        LOGGER.debug("Payload: {}", dto);
        PriceCategory entity = mapper.priceCategoryCreateToPriceCategory(dto);
        entity.setPrice(dto.getPrice());
        entity.setName(dto.getName());

        PriceCategory savedPriceCategory = repository.save(entity);
        return mapper.priceCategoryToPriceCategoryDto(savedPriceCategory);
    }

    @Override
    public void delete(Long id) {
        LOGGER.info("Deleting price category with id={}", id);
        repository.deleteById(id);
    }
}
