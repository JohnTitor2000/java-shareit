package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

@Service
public class BookingService {

    BookingRepository bookingRepository;
    UserRepository userRepository;

    @Autowired
    public BookingService(UserRepository userRepository,BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking, Long sharerId) {
        booking.setBooker(userRepository.findById(sharerId).orElseThrow(() -> new NotFoundException("User Not Found.")));
        return bookingRepository.save(booking);
    }
}
