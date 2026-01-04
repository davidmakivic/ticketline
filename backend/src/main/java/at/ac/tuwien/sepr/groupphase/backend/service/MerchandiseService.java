package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MerchandiseDto;

import java.util.List;

public interface MerchandiseService {

    List<MerchandiseDto> findAll();

    MerchandiseDto findById(Long id);

    MerchandiseDto save(MerchandiseDto merchandiseDto);

    void delete(Long id);

}
