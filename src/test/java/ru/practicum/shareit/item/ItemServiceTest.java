package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getItemByIdTest_Successes() {
        Item item = createItem(1);
        Comment comment = createComment(1);
        comment.setItem(item);
        List<Comment> comments = Collections.singletonList(comment);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsNext(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(1)));
        when(bookingRepository.findBookingsLast(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(1)));
        when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        when(itemMapper.itemToItemDtoWithBookings(any(Item.class), any(BookingIdAndBookerId.class), any(BookingIdAndBookerId.class), anyList())).thenReturn(itemToItemDtoWithBookings(item, new BookingIdAndBookerId(1L, 1L), new BookingIdAndBookerId(1L, 1L), comments));
    }

    @Test
    public void testGetAllItems_ReturnsNonEmptyList() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(new User());
        item1.setRequestId(100L);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(new User());
        item2.setRequestId(200L);

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(itemList);

        ItemDtoDefault itemDto1 = new ItemDtoDefault();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1 DTO");
        itemDto1.setDescription("Description 1");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(new User());
        itemDto1.setRequestId(100L);

        ItemDtoDefault itemDto2 = new ItemDtoDefault();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2 DTO");
        itemDto2.setDescription("Description 2");
        itemDto2.setAvailable(false);
        itemDto2.setOwner(new User());
        itemDto2.setRequestId(200L);

        List<ItemDtoDefault> expectedDtoList = Arrays.asList(itemDto1, itemDto2);

        when(itemMapper.itemToItemDtoDefault(item1)).thenReturn(itemDto1);
        when(itemMapper.itemToItemDtoDefault(item2)).thenReturn(itemDto2);

        List<ItemDtoDefault> result = itemService.getAllItems();

        assertEquals(expectedDtoList.size(), result.size());
        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(0).getName(), result.get(0).getName());
        assertEquals(expectedDtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getAvailable(), result.get(0).getAvailable());
        assertEquals(expectedDtoList.get(0).getOwner(), result.get(0).getOwner());
        assertEquals(expectedDtoList.get(0).getRequestId(), result.get(0).getRequestId());

        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());
        assertEquals(expectedDtoList.get(1).getName(), result.get(1).getName());
        assertEquals(expectedDtoList.get(1).getDescription(), result.get(1).getDescription());
        assertEquals(expectedDtoList.get(1).getAvailable(), result.get(1).getAvailable());
        assertEquals(expectedDtoList.get(1).getOwner(), result.get(1).getOwner());
        assertEquals(expectedDtoList.get(1).getRequestId(), result.get(1).getRequestId());
    }

    @Test
    public void testGetAllItems_ReturnsEmptyList() {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());
        List<ItemDtoDefault> result = itemService.getAllItems();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateItem_SuccessfulCreating() {

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequestId(100L);

        ItemDtoDefault itemDtoDefault = new ItemDtoDefault();
        itemDtoDefault.setId(1L);
        itemDtoDefault.setName("Item 1");
        itemDtoDefault.setDescription("Description 1");
        itemDtoDefault.setAvailable(true);
        itemDtoDefault.setOwner(user);
        itemDtoDefault.setRequestId(100L);

        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.itemToItemDtoDefault(any(Item.class))).thenReturn(itemDtoDefault);
        when(userService.getUserById(userId)).thenReturn(user);

        ItemDtoDefault result = itemService.createItem(item, 1L);

        assertEquals(itemDtoDefault.getId(), result.getId());
        assertEquals(itemDtoDefault.getName(), result.getName());
        assertEquals(itemDtoDefault.getDescription(), result.getDescription());
        assertEquals(itemDtoDefault.getAvailable(), result.getAvailable());
        assertEquals(itemDtoDefault.getOwner(), result.getOwner());
        assertEquals(itemDtoDefault.getRequestId(), result.getRequestId());
    }

    @Test
    public void testCreateItem_NotFoundUser() {
        Long userId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setRequestId(100L);

        when(userService.getUserById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> itemService.createItem(item, userId));
    }

    @Test
    public void testCreateItem_WithoutAvailable() {
        Long userId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setRequestId(100L);

        assertThrows(BadRequestException.class, () -> itemService.createItem(item, userId));
    }

    @Test
    public void testCreateItem_WithEmptyName() {
        Long userId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setName("");
        item.setDescription("Description 1");
        item.setAvailable(true);
        item.setRequestId(100L);

        assertThrows(BadRequestException.class, () -> itemService.createItem(item, userId));
    }

    @Test
    public void testCreateItem_WithEmptyDescription() {
        Long userId = 1L;

        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("");
        item.setAvailable(true);
        item.setRequestId(100L);

        assertThrows(BadRequestException.class, () -> itemService.createItem(item, userId));
    }

    @Test
    public void testSearch_EmptyText_ReturnsEmptyList() {
        String text = "";
        List<Item> itemList = Collections.emptyList();

        when(itemRepository.findByDescription(text)).thenReturn(itemList);

        List<ItemDtoDefault> result = itemService.search(text);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearch_NonEmptyText_ReturnsNonEmptyList() {
        String text = "search text";
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.findByDescription(text)).thenReturn(itemList);

        ItemDtoDefault itemDto1 = new ItemDtoDefault();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1 DTO");
        itemDto1.setDescription("Description 1");
        itemDto1.setAvailable(true);

        ItemDtoDefault itemDto2 = new ItemDtoDefault();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2 DTO");
        itemDto2.setDescription("Description 2");
        itemDto2.setAvailable(false);

        List<ItemDtoDefault> expectedDtoList = Arrays.asList(itemDto1, itemDto2);

        when(itemMapper.itemToItemDtoDefault(item1)).thenReturn(itemDto1);
        when(itemMapper.itemToItemDtoDefault(item2)).thenReturn(itemDto2);

        List<ItemDtoDefault> result = itemService.search(text);

        assertEquals(expectedDtoList.size(), result.size());
        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(0).getName(), result.get(0).getName());
        assertEquals(expectedDtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getAvailable(), result.get(0).getAvailable());

        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());
        assertEquals(expectedDtoList.get(1).getName(), result.get(1).getName());
        assertEquals(expectedDtoList.get(1).getDescription(), result.get(1).getDescription());
        assertEquals(expectedDtoList.get(1).getAvailable(), result.get(1).getAvailable());
    }

    @Test
    public void testSearch_SubstringInDescription_ReturnsMatchingItems() {
        String text = "description";

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("This is a description");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Another text");
        item2.setAvailable(false);

        Item item3 = new Item();
        item3.setId(3L);
        item3.setName("Item 3");
        item3.setDescription("Yet another description");
        item3.setAvailable(true);

        List<Item> itemList = Arrays.asList(item1, item3);

        when(itemRepository.findByDescription(text)).thenReturn(itemList);

        ItemDtoDefault itemDto1 = new ItemDtoDefault();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1 DTO");
        itemDto1.setDescription("This is a description");
        itemDto1.setAvailable(true);

        ItemDtoDefault itemDto3 = new ItemDtoDefault();
        itemDto3.setId(3L);
        itemDto3.setName("Item 3 DTO");
        itemDto3.setDescription("Yet another description");
        itemDto3.setAvailable(true);

        List<ItemDtoDefault> expectedDtoList = Arrays.asList(itemDto1, itemDto3);

        when(itemMapper.itemToItemDtoDefault(item1)).thenReturn(itemDto1);
        when(itemMapper.itemToItemDtoDefault(item3)).thenReturn(itemDto3);

        List<ItemDtoDefault> result = itemService.search(text);

        assertEquals(expectedDtoList.size(), result.size());
        assertEquals(expectedDtoList.get(0).getId(), result.get(0).getId());
        assertEquals(expectedDtoList.get(0).getName(), result.get(0).getName());
        assertEquals(expectedDtoList.get(0).getDescription(), result.get(0).getDescription());
        assertEquals(expectedDtoList.get(0).getAvailable(), result.get(0).getAvailable());

        assertEquals(expectedDtoList.get(1).getId(), result.get(1).getId());
        assertEquals(expectedDtoList.get(1).getName(), result.get(1).getName());
        assertEquals(expectedDtoList.get(1).getDescription(), result.get(1).getDescription());
        assertEquals(expectedDtoList.get(1).getAvailable(), result.get(1).getAvailable());
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

    private Comment createComment(int i) {
        Comment comment = new Comment();
        comment.setAuthor(createUser(i + 5));
        comment.setCreated(LocalDateTime.now().plusHours(1));
        comment.setId((long) i);
        comment.setText("Lorems" + i);
        return comment;
    }

    public ItemDtoWithBookings itemToItemDtoWithBookings(Item item, BookingIdAndBookerId last, BookingIdAndBookerId next, List<Comment> comment) {
        if (item == null) {
            return null;
        }
        List<CommentDto> comments = comment.stream().map(CommentMapper::commentToCommentDto).collect(Collectors.toList());
        ItemDtoWithBookings itemDtoWithBookings = ItemDtoWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
        itemDtoWithBookings.setComments(comments);
        itemDtoWithBookings.setLastBooking(last);
        itemDtoWithBookings.setNextBooking(next);
        return itemDtoWithBookings;
    }
}