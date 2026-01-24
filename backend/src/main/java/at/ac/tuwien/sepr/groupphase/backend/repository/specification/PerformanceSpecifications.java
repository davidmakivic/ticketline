package at.ac.tuwien.sepr.groupphase.backend.repository.specification;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Hall;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class PerformanceSpecifications {

    public static Specification<Performance> hasEventTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isEmpty()) {
                return cb.conjunction();
            }
            Join<Performance, Event> eventJoin = root.join("event", JoinType.INNER);
            return cb.like(cb.lower(eventJoin.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Performance> hasArtist(String artistName) {
        return (root, query, cb) -> {
            if (artistName == null || artistName.isEmpty()) {
                return cb.conjunction();
            }
            Join<Performance, Event> eventJoin = root.join("event", JoinType.INNER);
            Join<Event, Artist> artistJoin = eventJoin.join("artists", JoinType.INNER);
            return cb.or(
                cb.like(cb.lower(artistJoin.get("firstName")), "%" + artistName.toLowerCase() + "%"),
                cb.like(cb.lower(artistJoin.get("lastName")), "%" + artistName.toLowerCase() + "%"),
                cb.like(cb.lower(artistJoin.get("stageName")), "%" + artistName.toLowerCase() + "%")
            );
        };
    }

    public static Specification<Performance> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isEmpty()) {
                return cb.conjunction();
            }
            Join<Performance, Hall> hallJoin = root.join("hall", JoinType.INNER);
            Join<Hall, Venue> venueJoin = hallJoin.join("venue", JoinType.INNER);
            return cb.or(
                cb.like(cb.lower(venueJoin.get("name")), "%" + location.toLowerCase() + "%"),
                cb.like(cb.lower(venueJoin.get("city")), "%" + location.toLowerCase() + "%"),
                cb.like(cb.lower(venueJoin.get("country")), "%" + location.toLowerCase() + "%")
            );
        };
    }

    public static Specification<Performance> hasEventType(EventType eventType) {
        return (root, query, cb) -> {
            if (eventType == null) {
                return cb.conjunction();
            }
            Join<Performance, Event> eventJoin = root.join("event", JoinType.INNER);
            return cb.equal(eventJoin.get("category"), eventType);
        };
    }

    public static Specification<Performance> hasStartDateAfter(Date date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }

            // Start und Ende des Tages
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

            // Performance.startTime liegt zwischen startOfDay und endOfDay
            return cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), startOfDay),
                cb.lessThan(root.get("startTime"), endOfDay)
            );
        };
    }




    public static Specification<Performance> hasDuration(Integer durationMinutes) {
        return (root, query, cb) -> {
            if (durationMinutes == null) {
                return cb.conjunction();
            }
            Join<Performance, Event> eventJoin = root.join("event", JoinType.INNER);
            return cb.equal(eventJoin.get("durationMinutes"), durationMinutes);
        };
    }

    public static Specification<Performance> fetchDetails() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("event", JoinType.LEFT);
                root.fetch("hall", JoinType.LEFT).fetch("venue", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
