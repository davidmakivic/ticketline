package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;

import java.util.List;

public interface EventService {
    Event create(Event event);

    Event update(Long id, Event event);

    Event findById(Long id);

    List<Event> findAll();

    void delete(Long id);
}
