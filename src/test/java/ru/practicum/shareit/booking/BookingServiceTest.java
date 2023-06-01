package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking_Success() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(2));
        Long sharerId = 1L;
        Long itemId = 2L;

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(4L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(sharerId)).thenReturn(Optional.of(new User()));
        when(bookingMapper.bookingDtoOutputToBooking(bookingDtoInput, item, owner)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.bookingToBookingDtoOutput(booking)).thenReturn(new BookingDtoOutput());

        // Act
        BookingDtoOutput result = bookingService.createBooking(bookingDtoInput, sharerId);

        // Assert
        Assertions.assertNotNull(result);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void testCreateBooking_InvalidEndTimeInPast() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().minusDays(1));
        Long sharerId = 1L;

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidItemId() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(2));
        Long sharerId = 1L;
        Long itemId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }
}
