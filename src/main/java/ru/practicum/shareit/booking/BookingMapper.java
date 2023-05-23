package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public Booking bookingDtoOutputToBooking(BookingDtoInput bookingDtoInput, Item item, User booker) {
        Booking booking = new Booking();
        booking.setEnd(bookingDtoInput.getEnd());
        booking.setStart(bookingDtoInput.getStart());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public BookingDtoOutput bookingToBookingDtoOutput(Booking booking) {
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setItem(booking.getItem());
        bookingDtoOutput.setBooker(booking.getBooker());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setStatus(booking.getStatus());
        return bookingDtoOutput;
    }
}
