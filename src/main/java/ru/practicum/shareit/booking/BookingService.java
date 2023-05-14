package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    BookingMapper bookingMapper;

    @Autowired
    public BookingService(UserRepository userRepository,BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    public Booking createBooking(BookingDto bookingDto, Long sharerId) {
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("End time cant be in present.");
        } else if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("End time cant be before start time.");
        } else if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BadRequestException("End time and Start time cant be in same time.");
        } else if (bookingDto.getStart().equals(null)) {
            throw new BadRequestException("Start time cant be null");
        } else if (bookingDto.getEnd().equals(null)) {
            throw new BadRequestException("End time cant be null");
        } else if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start time cant be in present");
        }
        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto, sharerId);
        booking.setStatus(BookingStatus.WAITING);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item not available");
        }
        return bookingRepository.save(booking);
    }

    public Booking editBookingStatus(Long bookingId, boolean approve, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new BadRequestException("You cant edit this booking.");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (!userId.equals(booking.getBooker().getId()) && userId.equals(booking.getItem().getOwner().getId())) {
            throw new BadRequestException("You cant get this information.");
        }
        return booking;
    }

    public List<Booking> getBookingsByUserId(String state, Long userId) {
        List<Booking> bookings
    }
}
