package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.item i WHERE i.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findBookingsByItemOwnerId(Long ownerId);

    @Query("select booking "
            + " from Booking booking "
            + " where booking.start < CURRENT_TIMESTAMP "
            + " and booking.item.id in :ids "
            + " and booking.item.owner.id = :userId "
            + " and booking.status = 'APPROVED' "
            + " order by booking.start desc")
    List<Booking> findBookingsLast(@Param("ids") Long ids,
                                   @Param("userId") Long userId,
                                   Pageable pageable);

    @Query("select booking "
            + " from Booking booking "
            + " where booking.start > CURRENT_TIMESTAMP "
            + " and booking.item.id in :ids "
            + " and booking.item.owner.id = :userId "
            + " and booking.status = 'APPROVED' "
            + " order by booking.start asc")
    List<Booking> findBookingsNext(@Param("ids") Long ids,
                                   @Param("userId") Long userId,
                                   Pageable pageable);
}