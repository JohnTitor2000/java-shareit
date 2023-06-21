package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.exaption.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testCreateBooking_InvalidStartTimeIsNull() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(null);
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(1));
        Long sharerId = 1L;

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidEndBeforeStart() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(1));
        bookingDtoInput.setEnd(LocalDateTime.now().plusHours(2));
        Long sharerId = 1L;

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidEndAndStartInTheSameTime() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now());
        bookingDtoInput.setEnd(LocalDateTime.now());
        Long sharerId = 1L;

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidStartInPresent() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().minusDays(2));
        bookingDtoInput.setEnd(LocalDateTime.now().plusHours(1));
        Long sharerId = 1L;

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidUserId() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now().plusDays(2));
        bookingDtoInput.setEnd(LocalDateTime.now().plusDays(3));
        bookingDtoInput.setItemId(1L);
        Long sharerId = 1L;
        Item item = createItem(1);
        item.getOwner().setId(1L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // Act & Assert
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDtoInput, sharerId));
    }

    @Test
    public void testCreateBooking_InvalidEndTimeIsNull() {
        // Arrange
        BookingDtoInput bookingDtoInput = BookingDtoInput.builder().build();
        bookingDtoInput.setStart(LocalDateTime.now());
        bookingDtoInput.setEnd(null);
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
    public void testCreateBooking_InvalidItemNotAvailable() {
        Booking booking = createBooking(1);
        booking.getItem().setAvailable(false);
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

        Assertions.assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, booking.getBooker().getId()));
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
    public void testEditBookingStatus_Rejected() {
        Booking booking = createBooking(1);
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput();
        bookingDtoOutput.setId(booking.getId());
        bookingDtoOutput.setStart(booking.getStart());
        bookingDtoOutput.setEnd(booking.getEnd());
        bookingDtoOutput.setBooker(booking.getBooker());
        bookingDtoOutput.setStatus(BookingStatus.REJECTED);
        bookingDtoOutput.setItem(booking.getItem());

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingMapper.bookingToBookingDtoOutput(booking)).thenReturn(bookingDtoOutput);

        BookingDtoOutput result = bookingService.editBookingStatus(booking.getId(), false, booking.getItem().getOwner().getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getBooker(), result.getBooker());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    public void testEditBookingStatus_CantEdit() {
        Booking booking = createBooking(1);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userRepository.existsById(10L)).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.editBookingStatus(booking.getId(), true, 10L));
    }

    @Test
    public void testEditBookingStatus_AlreadyApproved() {
        Booking booking = createBooking(1);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(2L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.editBookingStatus(booking.getId(), true, booking.getItem().getOwner().getId()));
    }

    @Test
    public void testEditBookingStatus_UserNotFound() {
        Booking booking = createBooking(1);
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.editBookingStatus(booking.getId(), true, booking.getItem().getOwner().getId()));
    }

    @Test
    public void testGetBookingsByUserIdAll_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("ALL", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByUserIdCurrent_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        bookings.get(0).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(0).setEnd(LocalDateTime.now().plusDays(1));
        bookings.get(1).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(1).setEnd(LocalDateTime.now().plusDays(1));
        bookings.get(2).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(2).setEnd(LocalDateTime.now().plusDays(1));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("CURRENT", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByUserIdPast_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        bookings.get(0).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(0).setEnd(LocalDateTime.now().minusDays(1));
        bookings.get(1).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(1).setEnd(LocalDateTime.now().minusDays(1));
        bookings.get(2).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(2).setEnd(LocalDateTime.now().minusDays(1));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("PAST", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByUserIdFuture_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        bookings.get(0).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(0).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(1).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(1).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(2).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(2).setEnd(LocalDateTime.now().plusDays(2));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("FUTURE", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByUserIdWaiting_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        bookings.get(0).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(0).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(1).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(1).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(2).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(2).setEnd(LocalDateTime.now().plusDays(2));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("WAITING", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByUserIdRejected_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).setBooker(bookings.get(0).getBooker());
        bookings.get(2).setBooker(bookings.get(0).getBooker());
        bookings.get(0).setStatus(BookingStatus.REJECTED);
        bookings.get(1).setStatus(BookingStatus.REJECTED);
        bookings.get(2).setStatus(BookingStatus.REJECTED);
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(1L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByUserId("REJECTED", 1L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerWaiting_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("WAITING", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerRejected_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(0).setStatus(BookingStatus.REJECTED);
        bookings.get(1).setStatus(BookingStatus.REJECTED);
        bookings.get(2).setStatus(BookingStatus.REJECTED);
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("REJECTED", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.REJECTED, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerAll_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("ALL", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerCurrent_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(0).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(0).setEnd(LocalDateTime.now().plusDays(1));
        bookings.get(1).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(1).setEnd(LocalDateTime.now().plusDays(1));
        bookings.get(2).setStart(LocalDateTime.now().minusDays(1));
        bookings.get(2).setEnd(LocalDateTime.now().plusDays(1));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("CURRENT", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerFuture_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(0).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(0).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(1).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(1).setEnd(LocalDateTime.now().plusDays(2));
        bookings.get(2).setStart(LocalDateTime.now().plusDays(1));
        bookings.get(2).setEnd(LocalDateTime.now().plusDays(2));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("FUTURE", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    public void testGetBookingsByOwnerPast_GetThreeResults() {
        List<Booking> bookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        bookings.get(1).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(2).getItem().setOwner(bookings.get(0).getItem().getOwner());
        bookings.get(0).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(0).setEnd(LocalDateTime.now().minusDays(1));
        bookings.get(1).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(1).setEnd(LocalDateTime.now().minusDays(1));
        bookings.get(2).setStart(LocalDateTime.now().minusDays(2));
        bookings.get(2).setEnd(LocalDateTime.now().minusDays(1));
        Long userId = bookings.get(0).getBooker().getId();
        BookingDtoOutput book1 = bookingToBookingDtoOutput(bookings.get(0));
        BookingDtoOutput book2 = bookingToBookingDtoOutput(bookings.get(1));
        BookingDtoOutput book3 = bookingToBookingDtoOutput(bookings.get(2));

        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(0))).thenReturn(book1);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(1))).thenReturn(book2);
        when(bookingMapper.bookingToBookingDtoOutput(bookings.get(2))).thenReturn(book3);
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(bookings);
        when(userRepository.existsById(2L)).thenReturn(true);

        List<BookingDtoOutput> result = bookingService.getBookingsByOwnerId("PAST", 2L, 0, 10);

        assertEquals(result.size(), 3);
        assertEquals(bookings.get(0).getId(), result.get(0).getId());
        assertEquals(bookings.get(0).getBooker(), result.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, result.get(0).getStatus());
    }

    @Test
    void getBookingTest_RequestButNotOwner() {
        Booking booking = createBooking(1);

        when(userRepository.findById(10L)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(booking.getId(), 10L));
    }

    @Test
    void getBookingsByUserIdTest_FromAndSizeZero() {
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsByUserId("some", 1L, 0, 0));
    }

    @Test
    void getBookingsByUserIdTest_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByUserId("some", 1L, 1, 10));
    }

    @Test
    void getBookingsByUserId_UnsupportedStatusException() {

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getBookingsByUserId("some", 1L, 1, 10));
    }

    @Test
    void getBookingsByOwnerId_FromAndSizeZero() {
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsByOwnerId("some", 1L, 0, 0));
    }

    @Test
    void getBookingsByOwnerId_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwnerId("some", 1L, 1, 10));
    }

    @Test
    void getBookingsByOwnerId_UnsupportedStatusException() {

        when(userRepository.existsById(1L)).thenReturn(true);

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getBookingsByOwnerId("some", 1L, 1, 10));
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
