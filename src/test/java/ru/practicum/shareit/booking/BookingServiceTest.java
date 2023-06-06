package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.specifications.BookingSpecifications;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void testCreateBooking_Success() {
        Booking booking = createBooking(1);
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder()
                .itemId(booking.getItem().getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .build();
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setItem(booking.getItem());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setStatus(booking.getStatus());
        bookingDtoOutput.setBooker(booking.getBooker());
        bookingDtoOutput.setId(booking.getId());

        when(bookingRepository.save(booking)).thenReturn(booking);
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(booking.getItem()));
        when(userRepository.findById(booking.getBooker().getId())).thenReturn(Optional.of(booking.getBooker()));
        when(bookingMapper.bookingDtoInputToBooking(bookingDtoInput, booking.getItem(), booking.getBooker())).thenReturn(booking);
        when(bookingMapper.bookingToBookingDtoOutput(booking)).thenReturn(bookingDtoOutput);

        BookingDtoOutput result = bookingService.createBooking(bookingDtoInput, booking.getBooker().getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    public void testGetBooking_TargetBookingExist() {
        Booking booking = createBooking(1);
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setBooker(booking.getBooker());
        bookingDtoOutput.setStatus(booking.getStatus());
        bookingDtoOutput.setItem(booking.getItem());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booking.getItem().getOwner()));
        when(bookingMapper.bookingToBookingDtoOutput(booking)).thenReturn(bookingDtoOutput);

        BookingDtoOutput result = bookingService.getBooking(booking.getId(), booking.getItem().getOwner().getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(booking.getStatus(), result.getStatus());

    }

    @Test
    public void testEditBookingStatus_Success() {
        Booking booking = createBooking(1);
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setBooker(booking.getBooker());
        bookingDtoOutput.setStatus(BookingStatus.APPROVED);
        bookingDtoOutput.setItem(booking.getItem());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingMapper.bookingToBookingDtoOutput(booking)).thenReturn(bookingDtoOutput);

        BookingDtoOutput result = bookingService.editBookingStatus(booking.getId(), true, booking.getItem().getOwner().getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

        @Test
        public void testGetBookingsByUserId_GetThreeResults() {
            List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
            bookings.get(1).setBooker(bookings.get(0).getBooker());
            bookings.get(2).setBooker(bookings.get(0).getBooker());
            Long userId = bookings.get(0).getBooker().getId();
            Page<Booking> mockBookingPage = new PageImpl<>(bookings);

            Pageable pageable  = PageRequest.of(0, 100, Sort.Direction.DESC, "start");

            when(bookingRepository.findAll(BookingSpecifications.withBookerId(userId), pageable)).thenReturn(mockBookingPage);
            when(userRepository.existsById(1L)).thenReturn(true);

            List<BookingDtoOutput> result = bookingService.getBookingsByUserId("ALL", 1L, 0, 10);

            assertEquals(result.size(), 3);
            assertEquals(bookings.get(0).getId(), result.get(0).getId());
            assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
            assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
        }

    public void testGetBookingsByOwner_GetThreeResults() {

    }


    private Booking createBooking(int number) {
        Booking booking = new Booking();
        booking.setId((long) number);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(createUser(number));
        booking.setItem(createItem(number));
        return booking;
    }

    private User createUser(int number) {
        User user = new User();
        user.setId((long) number);
        user.setName("John Doe " + String.valueOf(number));
        user.setEmail("jd" + String.valueOf(number) + "@email.com");
        return user;
    }

    private Item createItem(int number) {
        Item item = new Item();
        item.setId((long) number);
        item.setName("things" + String.valueOf(number));
        item.setDescription("description" + String.valueOf(number));
        item.setAvailable(true);
        item.setOwner(createUser(number + 1));
        return item;
    }

    private Page<BookingDtoOutput> mapBookingsToDtoOutput(Page<Booking> bookings) {
        return bookings.map(bookingMapper::bookingToBookingDtoOutput);
    }
}
