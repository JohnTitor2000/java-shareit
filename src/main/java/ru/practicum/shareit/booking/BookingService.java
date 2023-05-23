package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.exaption.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.specifications.BookingSpecifications.*;

@Service
public class BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;
    private ItemRepository itemRepository;

    @Autowired
    public BookingService(UserRepository userRepository,BookingRepository bookingRepository, BookingMapper bookingMapper, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
    }

    public BookingDtoOutput createBooking(BookingDtoInput bookingDtoInput, Long sharerId) {
        if (bookingDtoInput.getStart() == null) {
            throw new BadRequestException("Start time cant be null");
        } else if (bookingDtoInput.getEnd() == null) {
            throw new BadRequestException("End time cant be null");
        } else if (bookingDtoInput.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("End time cant be in present.");
        } else if (bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart())) {
            throw new BadRequestException("End time cant be before start time.");
        } else if (bookingDtoInput.getEnd().equals(bookingDtoInput.getStart())) {
            throw new BadRequestException("End time and Start time cant be in same time.");
        } else if (bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start time cant be in present");
        } else if (sharerId.equals(itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() -> new NotFoundException("User not found.")).getOwner().getId())) {
            throw new NotFoundException("You cant booking this item.");
        }
        Booking booking = bookingMapper.bookingDtoOutputToBooking(bookingDtoInput,
                itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() -> new NotFoundException("Item not found")),
                userRepository.findById(sharerId).orElseThrow(() -> new NotFoundException("User not found")));
        booking.setStatus(BookingStatus.WAITING);
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item not available");
        }
        return bookingMapper.bookingToBookingDtoOutput(bookingRepository.save(booking));
    }

    public BookingDtoOutput editBookingStatus(Long bookingId, boolean approve, Long ownerId) {
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
        return bookingMapper.bookingToBookingDtoOutput(bookingRepository.save(booking));
    }

    public BookingDtoOutput getBooking(Long bookingId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("user not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (!(userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId()))) {
            throw new NotFoundException("You cant get this information.");
        }
        return bookingMapper.bookingToBookingDtoOutput(booking);
    }

    public List<BookingDtoOutput> getBookingsByUserId(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                return bookingRepository.findAll(withBookerId(userId), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAll(withBookerId(userId)
                        .and(startBeforeCurrentTime())
                        .and(endAfterCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAll(withBookerId(userId)
                        .and(endBeforeCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "FUTURE":
                return  bookingRepository.findAll(withBookerId(userId)
                        .and(startAfterCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAll(withBookerId(userId)
                        .and(withStatus(BookingStatus.WAITING)), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAll(withBookerId(userId)
                        .and(withStatus(BookingStatus.REJECTED)), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    public List<BookingDtoOutput> getBookingsByOwnerId(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case "ALL":
                return bookingRepository.findAll(withOwnerId(userId), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findAll(withOwnerId(userId)
                        .and(startBeforeCurrentTime())
                        .and(endAfterCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findAll(withOwnerId(userId)
                        .and(endBeforeCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "FUTURE":
                return  bookingRepository.findAll(withOwnerId(userId)
                        .and(startAfterCurrentTime()), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAll(withOwnerId(userId)
                        .and(withStatus(BookingStatus.WAITING)), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAll(withOwnerId(userId)
                        .and(withStatus(BookingStatus.REJECTED)), sort).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }
}