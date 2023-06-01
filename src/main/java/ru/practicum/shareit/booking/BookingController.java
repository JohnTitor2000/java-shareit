package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

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
    public BookingDtoOutput addBooking(@RequestBody BookingDtoInput bookingDtoInput, @RequestHeader(name = "X-Sharer-User-Id") Long sharerId) {
        return bookingService.createBooking(bookingDtoInput, sharerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput editBookingStatus(@PathVariable("bookingId") Long bookingId, @RequestParam("approved") boolean approved, @RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return bookingService.editBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return  bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDtoOutput> getBookingsByUserId(@RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                      @RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByUserId(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getBookingsByOwnerId(@RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                       @RequestParam(required = false, defaultValue = "ALL") String state, @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingsByOwnerId(state, userId, from, size);
    }
}
