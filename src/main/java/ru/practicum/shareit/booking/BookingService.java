package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.exaption.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    BookingMapper bookingMapper;
    ItemRepository itemRepository;

    @Autowired
    public BookingService(UserRepository userRepository,BookingRepository bookingRepository, BookingMapper bookingMapper, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
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
        } else if (sharerId.equals(itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("User not found.")).getOwner().getId())) {
            throw new NotFoundException("You cant booking this item.");
        }
        Booking booking = bookingMapper.bookingDtoToBooking(bookingDto, sharerId);
        booking.setStatus(BookingStatus.WAITING);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item not available");
        }
        return bookingRepository.save(booking);
    }

    public Booking editBookingStatus(Long bookingId, boolean approve, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("User not found.");
        }
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Booking already approved");
        }
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("You cant edit this booking.");
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId()))) {
            throw new NotFoundException("You cant get this information.");
        }
        return booking;
    }

    public List<Booking> getBookingsByUserId(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
        switch (state) {
            case "ALL":
                return bookings;
            case "CURRENT":
                return bookings.stream().filter((o) -> o.getStart().isBefore(LocalDateTime.now()) && o.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter((o) -> o.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
            case "FUTURE":
                return  bookings.stream().filter(o -> o.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case "WAITING":
                return bookings.stream().filter(o -> o.getStatus().equals(BookingStatus.WAITING)).collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(o -> o.getStatus().equals(BookingStatus.REJECTED)).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    public List<Booking> getBookingsByOwnerId(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerId(userId);
        switch (state) {
            case "ALL":
                return bookings;
            case "CURRENT":
                return bookings.stream().filter((o) -> o.getStart().isBefore(LocalDateTime.now()) && o.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case "PAST":
                return bookings.stream().filter((o) -> o.getEnd().isBefore(LocalDateTime.now())).collect(Collectors.toList());
            case "FUTURE":
                return  bookings.stream().filter(o -> o.getStart().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case "WAITING":
                return bookings.stream().filter(o -> o.getStatus().equals(BookingStatus.WAITING)).collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream().filter(o -> o.getStatus().equals(BookingStatus.REJECTED)).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }
}