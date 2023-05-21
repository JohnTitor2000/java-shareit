package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@Component
public class BookingMapper {

    ItemService itemService;
    UserService userService;

    ItemRepository itemRepository;

    @Autowired
    public BookingMapper(UserService userService, ItemService itemService, ItemRepository itemRepository) {
        this.userService = userService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    public Booking bookingDtoToBooking(BookingDto bookingDto, Long sharerId) {
        Booking booking = new Booking();
        booking.setEnd(bookingDto.getEnd());
        booking.setStart(bookingDto.getStart());
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found")));
        booking.setBooker(userService.getUserById(sharerId));
        return booking;
    }

    public BookingDto bookingToBookingDto(Booking booking) {
        return null;
    }
}
