package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.specifications.BookingSpecifications;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.exaption.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private BookingMapper bookingMapper;
    private ItemRepository itemRepository;

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
        Booking booking = bookingMapper.bookingDtoInputToBooking(bookingDtoInput,
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

    public List<BookingDtoOutput> getBookingsByUserId(String state, Long userId, Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new BadRequestException("cant");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }

        switch (state) {
            case "ALL":
                List<BookingDtoOutput> bookings = bookingRepository.findAll(BookingSpecifications.withBookerId(userId)).stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
                return bookings.stream().skip(from).limit(size).collect(Collectors.toList());
            case "CURRENT":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withBookerId(userId)
                        .and(BookingSpecifications.startBeforeCurrentTime())
                        .and(BookingSpecifications.endAfterCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "PAST":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withBookerId(userId)
                        .and(BookingSpecifications.endBeforeCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "FUTURE":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withBookerId(userId)
                        .and(BookingSpecifications.startAfterCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "WAITING":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withBookerId(userId)
                        .and(BookingSpecifications.withStatus(BookingStatus.WAITING)))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "REJECTED":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withBookerId(userId)
                        .and(BookingSpecifications.withStatus(BookingStatus.REJECTED)))).stream().skip(from).limit(size).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    public List<BookingDtoOutput> getBookingsByOwnerId(String state, Long userId, Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new BadRequestException("cant");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found.");
        }
        switch (state) {
            case "ALL":
                List<Booking> bookings = bookingRepository.findAll(BookingSpecifications.withOwnerId(userId));
                List<BookingDtoOutput> bookingsAfterMap = bookings.stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
                List<BookingDtoOutput> result = bookingsAfterMap.stream().skip(from).limit(size).collect(Collectors.toList());
                return result;
            case "CURRENT":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withOwnerId(userId)
                        .and(BookingSpecifications.startBeforeCurrentTime())
                        .and(BookingSpecifications.endAfterCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "PAST":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withOwnerId(userId)
                        .and(BookingSpecifications.endBeforeCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "FUTURE":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withOwnerId(userId)
                        .and(BookingSpecifications.startAfterCurrentTime()))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "WAITING":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withOwnerId(userId)
                        .and(BookingSpecifications.withStatus(BookingStatus.WAITING)))).stream().skip(from).limit(size).collect(Collectors.toList());
            case "REJECTED":
                return mapBookingsToDtoOutput(bookingRepository.findAll(BookingSpecifications.withOwnerId(userId)
                        .and(BookingSpecifications.withStatus(BookingStatus.REJECTED)))).stream().skip(from).limit(size).collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException(state);
        }
    }

    public List<BookingDtoOutput> mapBookingsToDtoOutput(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
    }
}

