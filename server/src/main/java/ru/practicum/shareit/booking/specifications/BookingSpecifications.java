package ru.practicum.shareit.booking.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class BookingSpecifications {
    public static Specification<Booking> startAfterCurrentTime() {
        return (root, query, cd) -> cd.greaterThan(root.get("start"), LocalDateTime.now());
    }

    public static Specification<Booking> startBeforeCurrentTime() {
        return (root, query, cd) -> cd.lessThan(root.get("start"), LocalDateTime.now());
    }

    public static Specification<Booking> endAfterCurrentTime() {
        return (root, query, cd) -> cd.greaterThan(root.get("end"), LocalDateTime.now());
    }

    public static Specification<Booking> endBeforeCurrentTime() {
        return (root, query, cd) -> cd.lessThan(root.get("end"), LocalDateTime.now());
    }

    public static Specification<Booking> withStatus(BookingStatus bookingStatus) {
        return (root, query, cd) -> cd.equal(root.get("status"), bookingStatus);
    }

    public static Specification<Booking> withOwnerId(Long ownerId) {
        return (root, query, cb) -> {
            Join<Booking, Item> itemJoin = root.join("item", JoinType.LEFT);
            query.distinct(true);

            Predicate ownerIdPredicate = cb.equal(itemJoin.get("owner").get("id"), ownerId);
            query.orderBy(cb.desc(root.get("start")));

            return ownerIdPredicate;
        };
    }

    public static Specification<Booking> withBookerId(Long bookerId) {
        return (root, query, cb) -> {
            Predicate bookerIdPredicate = cb.equal(root.get("booker").get("id"), bookerId);
            query.orderBy(cb.desc(root.get("start")));
            return bookerIdPredicate;
        };
    }
}
