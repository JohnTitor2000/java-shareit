package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBooking() throws Exception {
        Booking expectedBooking = createBooking(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

        BookingDtoOutput outputBooking = new BookingDtoOutput();
        outputBooking.setId(expectedBooking.getId());
        outputBooking.setEnd(expectedBooking.getEnd());
        outputBooking.setStart(expectedBooking.getStart());
        outputBooking.setStatus(expectedBooking.getStatus());
        outputBooking.setBooker(expectedBooking.getBooker());
        outputBooking.setItem(expectedBooking.getItem());

        when(bookingService.createBooking(any(BookingDtoInput.class), anyLong())).thenReturn(outputBooking);

        mockMvc.perform(post("/bookings").header("X-Sharer-User-Id", expectedBooking.getBooker().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1, \"start\": \"" + expectedBooking.getStart() + "\", \"end\": \"" + expectedBooking.getEnd() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outputBooking.getId()))
                .andExpect(jsonPath("$.end").value(outputBooking.getEnd().format(formatter)))
                .andExpect(jsonPath("$.start").value(outputBooking.getStart().format(formatter)))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(outputBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outputBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outputBooking.getItem().getName()));
    }

    @Test
    void editBookingStatus() throws Exception {
        Booking expectedBooking = createBooking(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingDtoOutput outputBooking = new BookingDtoOutput();
        outputBooking.setId(expectedBooking.getId());
        outputBooking.setEnd(LocalDateTime.parse(expectedBooking.getEnd().format(formatter)));
        outputBooking.setStart(LocalDateTime.parse(expectedBooking.getStart().format(formatter)));
        outputBooking.setStatus(BookingStatus.APPROVED);
        outputBooking.setBooker(expectedBooking.getBooker());
        outputBooking.setItem(expectedBooking.getItem());

        when(bookingService.editBookingStatus(1L, true, 1L)).thenReturn(outputBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", expectedBooking.getId())
                        .header("X-Sharer-User-Id", expectedBooking.getBooker().getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outputBooking.getId()))
                .andExpect(jsonPath("$.end").value(outputBooking.getEnd().format(formatter)))
                .andExpect(jsonPath("$.start").value(outputBooking.getStart().format(formatter)))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.booker.id").value(outputBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outputBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outputBooking.getItem().getName()));
    }

    @Test
    void getBooking() throws Exception {
        Booking expectedBooking = createBooking(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingDtoOutput outputBooking = new BookingDtoOutput();
        outputBooking.setId(expectedBooking.getId());
        outputBooking.setEnd(LocalDateTime.parse(expectedBooking.getEnd().format(formatter)));
        outputBooking.setStart(LocalDateTime.parse(expectedBooking.getStart().format(formatter)));
        outputBooking.setStatus(BookingStatus.APPROVED);
        outputBooking.setBooker(expectedBooking.getBooker());
        outputBooking.setItem(expectedBooking.getItem());

        when(bookingService.getBooking(1L, 1L)).thenReturn(outputBooking);

        mockMvc.perform(get("/bookings/{bookingId}", expectedBooking.getId())
                        .header("X-Sharer-User-Id", expectedBooking.getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outputBooking.getId()))
                .andExpect(jsonPath("$.end").value(outputBooking.getEnd().format(formatter)))
                .andExpect(jsonPath("$.start").value(outputBooking.getStart().format(formatter)))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.booker.id").value(outputBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outputBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outputBooking.getItem().getName()));
    }

    @Test
    void getBookingsByUserId() throws Exception {
        List<Booking> expectedBookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        expectedBookings.get(1).setBooker(expectedBookings.get(0).getBooker());
        expectedBookings.get(2).setBooker(expectedBookings.get(0).getBooker());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingMapper bookingMapper = new BookingMapper();

        List<BookingDtoOutput> outputList = expectedBookings.stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
        outputList.forEach(o -> o.setEnd(LocalDateTime.parse(o.getEnd().format(formatter))));
        outputList.forEach(o -> o.setStart(LocalDateTime.parse(o.getStart().format(formatter))));

        when(bookingService.getBookingsByUserId("ALL", 1L, 0, 10)).thenReturn(outputList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", expectedBookings.get(0).getBooker().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].end").value(outputList.get(0).getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].start").value(outputList.get(0).getStart().format(formatter)))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(outputList.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(outputList.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(outputList.get(0).getItem().getName()));
    }

    @Test
    void getBookingsByOwnerId() throws Exception {
        List<Booking> expectedBookings = Arrays.asList(createBooking(1), createBooking(2), createBooking(3));
        expectedBookings.get(1).setItem(expectedBookings.get(0).getItem());
        expectedBookings.get(2).setItem(expectedBookings.get(0).getItem());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingMapper bookingMapper = new BookingMapper();

        List<BookingDtoOutput> outputList = expectedBookings.stream().map(bookingMapper::bookingToBookingDtoOutput).collect(Collectors.toList());
        outputList.forEach(o -> o.setEnd(LocalDateTime.parse(o.getEnd().format(formatter))));
        outputList.forEach(o -> o.setStart(LocalDateTime.parse(o.getStart().format(formatter))));

        when(bookingService.getBookingsByOwnerId("ALL", 2L, 0, 10)).thenReturn(outputList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].end").value(outputList.get(0).getEnd().format(formatter)))
                .andExpect(jsonPath("$[0].start").value(outputList.get(0).getStart().format(formatter)))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].booker.id").value(outputList.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(outputList.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(outputList.get(0).getItem().getName()));
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
}