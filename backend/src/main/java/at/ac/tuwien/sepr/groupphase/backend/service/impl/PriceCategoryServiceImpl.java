package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PriceCategoryUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PriceCategoryMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.PriceCategory;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PriceCategoryRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PriceCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceCategoryServiceImpl implements PriceCategoryService {

    private final PriceCategoryRepository repository;
    private final PriceCategoryMapper mapper;

    public PriceCategoryServiceImpl(PriceCategoryRepository repository, PriceCategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<PriceCategoryDto> findAll() {
        List<PriceCategory> entities = repository.findAll();
        return mapper.priceCategoryListToPriceCategoryDtoList(entities);
    }

    @Override
    public PriceCategoryDto findById(Long id) {
        return repository.findById(id)
            .map(mapper::priceCategoryToPriceCategoryDto)
            .orElse(null);
    }

    @Override
    public PriceCategoryDto update(Long id, PriceCategoryUpdateDto dto) {
        /*PriceCategory entity = mapper.priceCategoryDtoToPriceCategory(dto);
        PriceCategory saved = repository.save(entity);
        return mapper.priceCategoryToPriceCategoryDto(saved);*/

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
        PriceCategory entity = mapper.priceCategoryCreateToPriceCategory(dto);
        entity.setPrice(dto.getPrice());
        entity.setName(dto.getName());

        PriceCategory savedPriceCategory = repository.save(entity);
        return mapper.priceCategoryToPriceCategoryDto(savedPriceCategory);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
