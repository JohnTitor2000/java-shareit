package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@Component
public class BookingMapper {

    ItemService itemService;
    UserService userService;

    @Autowired
    public BookingMapper(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public Booking bookingDtoToBooking(BookingDto bookingDto, Long sharerId) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setItem(itemService.getItemById(bookingDto.getItemId()));
        booking.setBooker(userService.getUserById(sharerId));
        return booking;
    }

    public BookingDto bookingToBookingDto(Booking booking) {
        return null;
    }
}
