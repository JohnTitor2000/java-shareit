package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    public void testBookingDtoOutputToBooking() {
        BookingMapper bookingMapper = new BookingMapper();
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .build();

        Item item = Mockito.mock(Item.class);
        User booker = Mockito.mock(User.class);

        Booking booking = bookingMapper.bookingDtoOutputToBooking(bookingDtoInput, item, booker);

        assertEquals(bookingDtoInput.getStart(), booking.getStart());
        assertEquals(bookingDtoInput.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
    }


    @Test
    public void testBookingToBookingDtoOutput() {
        BookingMapper bookingMapper = new BookingMapper();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusHours(2));
        Item item = Mockito.mock(Item.class);
        User booker = Mockito.mock(User.class);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDtoOutput bookingDtoOutput = bookingMapper.bookingToBookingDtoOutput(booking);

        assertEquals(booking.getId(), bookingDtoOutput.getId());
        assertEquals(booking.getStart(), bookingDtoOutput.getStart());
        assertEquals(booking.getEnd(), bookingDtoOutput.getEnd());
        assertEquals(booking.getItem(), bookingDtoOutput.getItem());
        assertEquals(booking.getBooker(), bookingDtoOutput.getBooker());
        assertEquals(booking.getStatus(), bookingDtoOutput.getStatus());
    }
}

