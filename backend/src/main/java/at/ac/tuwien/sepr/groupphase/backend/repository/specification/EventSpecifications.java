package at.ac.tuwien.sepr.groupphase.backend.repository.specification;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepr.groupphase.backend.entity.Event;
import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Venue;
import at.ac.tuwien.sepr.groupphase.backend.type.EventType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class EventSpecifications {

    public static Specification<Event> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")),
                "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasArtist(String artist) {
        return (root, query, cb) -> {
            if (artist == null || artist.isBlank()) {
                return null;
            }

            Join<Event, Artist> artistJoin = root.join("artists", JoinType.LEFT);
            String pattern = "%" + artist.toLowerCase() + "%";

            return cb.or(
                cb.like(cb.lower(artistJoin.get("firstName")), pattern),
                cb.like(cb.lower(artistJoin.get("lastName")), pattern),
                cb.like(cb.lower(artistJoin.get("stageName")), pattern)
            );
        };
    }

    public static Specification<Event> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return null;
            }

            Join<Object, Object> performanceJoin = root.join("performances", JoinType.LEFT);
            Join<Object, Object> hallJoin = performanceJoin.join("hall", JoinType.LEFT);
            Join<Object, Object> venueJoin = hallJoin.join("venue", JoinType.LEFT);
            String pattern = "%" + location.toLowerCase() + "%";

            return cb.or(
                cb.like(cb.lower(hallJoin.get("name")), pattern),
                cb.like(cb.lower(venueJoin.get("city")), pattern),
                cb.like(cb.lower(venueJoin.get("street")), pattern),
                cb.like(cb.lower(venueJoin.get("country")), pattern),
                cb.like(cb.lower(venueJoin.get("postalCode")), pattern)
            );
        };
    }


    public static Specification<Event> hasEventType(EventType eventType) {
        return (root, query, cb) -> {
            if (eventType == null) {
                return null;
            }
            return cb.equal(root.get("category"), eventType);
        };
    }

    public static Specification<Event> hasStartDate(Date startDate) {
        return (root, query, cb) -> {
            if (startDate == null) {
                return null;
            }

            Join<Event, Performance> perfJoin = root.join("performances", JoinType.LEFT);
            return cb.equal(
                cb.function("DATE", Date.class, perfJoin.get("startTime")),
                cb.function("DATE", Date.class, cb.literal(startDate))
            );
        };
    }

    public static Specification<Event> hasDuration(Integer durationMinutes) {
        return (root, query, cb) -> {
            if (durationMinutes == null) {
                return null;
            }

            return cb.and(
                cb.greaterThanOrEqualTo(root.get("durationMinutes"), durationMinutes - 30),
                cb.lessThanOrEqualTo(root.get("durationMinutes"), durationMinutes + 30)
            );
        };
    }

    public static Specification<Event> fetchPerformances() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("performances", JoinType.LEFT);
                root.fetch("artists", JoinType.LEFT);
            }
            query.distinct(true);
            return null;
        };
    }
}
