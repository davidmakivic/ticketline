package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepr.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchandiseServiceImpl implements MerchandiseService {

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

}
