package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.Event;

import java.util.List;

public interface EventService {
    Event create(Event event);

    Event update(Event event);

    Event findById(int id);

    List<Event> findAll();

    void delete(int id);
}
