package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByEndTime(Long bookerId);

}
