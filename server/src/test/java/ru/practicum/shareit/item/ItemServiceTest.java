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
import ru.practicum.shareit.user.UserRepository;
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

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getItemById_Success() {

        Booking booking = createBooking(1);
        Item item = booking.getItem();

        ItemDtoWithBookings expected = new ItemDtoWithBookings();
        expected.setId(item.getId());
        expected.setAvailable(item.getAvailable());
        expected.setName(item.getName());
        expected.setOwner(item.getOwner());
        expected.setDescription(item.getDescription());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingsNext(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(2)));
        when(bookingRepository.findBookingsLast(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(3)));
        when(commentRepository.findByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(itemMapper.itemToItemDtoWithBookings(any(Item.class), any(BookingIdAndBookerId.class), any(BookingIdAndBookerId.class), anyList())).thenReturn(expected);

        ItemDtoWithBookings actual = itemService.getItemById(item.getId(), item.getOwner().getId());

        assertEquals(actual.getId(), expected.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getOwner(), actual.getOwner());

    }

    @Test
    void getItemByUser_Success() {

        Booking booking = createBooking(1);
        Item item = booking.getItem();

        ItemDtoWithBookings expected = new ItemDtoWithBookings();
        expected.setId(item.getId());
        expected.setAvailable(item.getAvailable());
        expected.setName(item.getName());
        expected.setOwner(item.getOwner());
        expected.setDescription(item.getDescription());

        when(userService.getUserById(anyLong())).thenReturn(item.getOwner());
        when(itemRepository.findByOwnerIdOrderById(anyLong())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findBookingsNext(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(2)));
        when(bookingRepository.findBookingsLast(anyLong(), anyLong(), any(PageRequest.class))).thenReturn(Collections.singletonList(createBooking(3)));
        when(commentRepository.findByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(itemMapper.itemToItemDtoWithBookings(any(Item.class), any(BookingIdAndBookerId.class), any(BookingIdAndBookerId.class), anyList())).thenReturn(expected);

        List<ItemDtoWithBookings> actual = itemService.getItemsByUser(item.getOwner().getId());

        assertEquals(actual.get(0).getId(), expected.getId());
        assertEquals(expected.getDescription(), actual.get(0).getDescription());
        assertEquals(expected.getName(), actual.get(0).getName());
        assertEquals(expected.getOwner(), actual.get(0).getOwner());

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

    @Test
    void addComment_EmptyText() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        assertThrows(BadRequestException.class, () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void addComment_BadRequest() {
        Comment comment = createComment(1);
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());

        Booking booking = createBooking(1);
        booking.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingRepository.findByBookerIdOrderByStartDesc(comment.getAuthor().getId())).thenReturn(Collections.singletonList(booking));

        assertThrows(BadRequestException.class, () -> itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void addComment_Success() {
        Booking booking = createBooking(1);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        Comment comment = createComment(1);
        CommentDto expected = new CommentDto();
        expected.setId(comment.getId());
        expected.setText(comment.getText());
        expected.setCreated(comment.getCreated());
        expected.setAuthorName(comment.getAuthor().getName());
        Item item = booking.getItem();
        User booker = booking.getBooker();
        User owner = item.getOwner();
        comment.setAuthor(booker);

        when(commentMapper.commentDtoToComment(expected, booker, item)).thenReturn(comment);
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentDto(comment)).thenReturn(expected);
        when(bookingRepository.findByBookerIdOrderByStartDesc(booker.getId())).thenReturn(Collections.singletonList(booking));

        CommentDto actual = itemService.addComment(item.getId(), booker.getId(), expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getAuthorName(), actual.getAuthorName());
    }

    @Test
    void updateItemTest_RequestByNotItemOwner() {
        Item item = createItem(1);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, new ItemDtoWithBookings(), 1L));
    }

    @Test
    void updateItemTest_NullName() {
        Item item = createItem(1);
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);
        itemDtoWithBookings.setName("new item");
        itemDtoWithBookings.setDescription("new description");
        itemDtoWithBookings.setAvailable(true);
        ItemDtoDefault expected = new ItemDtoDefault();
        expected.setId(item.getId());
        expected.setAvailable(item.getAvailable());
        expected.setName(item.getName());
        expected.setDescription(item.getDescription());
        expected.setOwner(item.getOwner());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.itemToItemDtoDefault(item)).thenReturn(expected);

        ItemDtoDefault result = itemService.updateItem(item.getId(), itemDtoWithBookings, item.getOwner().getId());

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getDescription(), result.getDescription());
    }

    @Test
    void deleteTest() {
        itemService.deleteItem(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateItemTest_ItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, new ItemDtoWithBookings(), 1L));
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
        CommentMapper commentMapper1 = new CommentMapper();
        if (item == null) {
            return null;
        }
        List<CommentDto> comments = comment.stream().map(commentMapper1::commentToCommentDto).collect(Collectors.toList());
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