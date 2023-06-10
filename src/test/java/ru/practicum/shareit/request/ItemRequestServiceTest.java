package ru.practicum.shareit.request;

import org.checkerframework.checker.optional.qual.MaybePresent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestServiceTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRequestMapper itemRequestMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveRequest_DescriptionIsNull() {
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
        itemRequestDtoInput.setDescription(null);

        assertThrows(BadRequestException.class, () -> itemRequestService.saveRequest(itemRequestDtoInput, 1L));
    }

    @Test
    void saveRequest_Success() {
        ItemRequest itemRequest = createItemRequest(1);
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
        itemRequestDtoInput.setDescription(itemRequest.getDescription());

        when(itemRequestMapper.itemRequestDtoInputToItemRequest(itemRequestDtoInput, itemRequest.getRequester())).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest.getRequester()));

        ItemRequest result = itemRequestService.saveRequest(itemRequestDtoInput, itemRequest.getId());

        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequester(), result.getRequester());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }

    @Test
    void getRequestById() {
        ItemRequest itemRequest = createItemRequest(1);
        ItemRequestOutput itemRequestOutput = ItemRequestOutput.builder()
                .id(itemRequest.getId())
                .requester(itemRequest.getRequester())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

        when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(null);
        when(userRepository.existsById(itemRequest.getRequester().getId())).thenReturn(true);
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest, null)).thenReturn(itemRequestOutput);

        ItemRequestOutput result =  itemRequestService.getRequestById(itemRequest.getId(), itemRequest.getRequester().getId());

        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getRequester(), result.getRequester());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }

    @Test
    void getRequests() {
        ItemRequest itemRequest1 = createItemRequest(1);
        ItemRequest itemRequest2 = createItemRequest(2);
        itemRequest2.setRequester(itemRequest1.getRequester());
        ItemRequestOutput itemRequestOutput1 = ItemRequestOutput.builder()
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .created(itemRequest1.getCreated())
                .requester(itemRequest1.getRequester())
                .build();
        ItemRequestOutput itemRequestOutput2 = ItemRequestOutput.builder()
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .created(itemRequest2.getCreated())
                .requester(itemRequest2.getRequester())
                .build();

        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        when(itemRequestRepository.findByRequesterId(itemRequest1.getRequester().getId())).thenReturn(itemRequests);
        when(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest1, null)).thenReturn(itemRequestOutput1);
        when(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest2, null)).thenReturn(itemRequestOutput2);
        when(itemRepository.findByRequestId(itemRequest1.getId())).thenReturn(null);
        when(itemRepository.findByRequestId(itemRequest2.getId())).thenReturn(null);
        when(userRepository.existsById(itemRequest1.getRequester().getId())).thenReturn(true);

        List<ItemRequestOutput> expected = Arrays.asList(itemRequestOutput1, itemRequestOutput2);
        List<ItemRequestOutput> actual = itemRequestService.getRequests(itemRequest1.getRequester().getId());

        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(0).getRequester(), actual.get(0).getRequester());
        assertEquals(expected.get(0).getDescription(), actual.get(0).getDescription());
    }

    @Test
    void getRequests_userNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequests(1L));
    }

    @Test
    void getAllRequests() {
        ItemRequest itemRequest1 = createItemRequest(1);
        ItemRequest itemRequest2 = createItemRequest(2);
        itemRequest2.setRequester(itemRequest1.getRequester());
        ItemRequestOutput itemRequestOutput1 = ItemRequestOutput.builder()
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .created(itemRequest1.getCreated())
                .requester(itemRequest1.getRequester())
                .build();
        ItemRequestOutput itemRequestOutput2 = ItemRequestOutput.builder()
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .created(itemRequest2.getCreated())
                .requester(itemRequest2.getRequester())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> itemRequestsPage = new PageImpl<>(Arrays.asList(itemRequest1, itemRequest2));

        when(itemRequestRepository.findAllByOrderByCreatedDesc(pageable)).thenReturn(itemRequestsPage);
        when(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest1, null)).thenReturn(itemRequestOutput1);
        when(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest2, null)).thenReturn(itemRequestOutput2);
        when(itemRepository.findByRequestId(itemRequest1.getId())).thenReturn(null);
        when(itemRepository.findByRequestId(itemRequest2.getId())).thenReturn(null);

        List<ItemRequestOutput> expected = Arrays.asList(itemRequestMapper.itemRequestToItemRequestOutput(itemRequest1, null), itemRequestMapper.itemRequestToItemRequestOutput(itemRequest2, null));
        List<ItemRequestOutput> actual = itemRequestService.getAllRequests(0, 10, 10L);

        assertEquals(expected.get(0).getId(), actual.get(0).getId());
        assertEquals(expected.get(0).getRequester(), actual.get(0).getRequester());
        assertEquals(expected.get(0).getDescription(), actual.get(0).getDescription());
    }

    private ItemRequest createItemRequest(int num) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(createUser(num + 3));
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("lorems" + num);
        itemRequest.setId((long) num);
        return itemRequest;
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
}