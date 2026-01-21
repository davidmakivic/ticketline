package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface ReservationService {

    ReservationDto create(ReservationCreateDto dto) throws ValidationException, ConflictException;

    List<ReservationDto> getAllForUser(String email);

    void deleteForUser(long reservationId, String email);
}
