package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking addBooking(@RequestBody BookingDto bookingDto, @RequestHeader(name = "X-Sharer-User-Id") Long sharerId) {
        return bookingService.createBooking(bookingDto, sharerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking editBookingStatus(@PathVariable("bookingId") Long bookingId, @RequestParam("approved") boolean approved, @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return bookingService.editBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return  bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<Booking> getBookingsByUserId(@RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwnerId(@RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByOwnerId(state, userId);
    }
}
