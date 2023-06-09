package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRequest() throws Exception {
        ItemRequest itemRequest = createItemRequest(1);
        itemRequest.setCreated(LocalDateTime.parse(itemRequest.getCreated().format(formatter)));
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
        itemRequestDtoInput.setDescription(itemRequest.getDescription());
        when(itemRequestService.saveRequest(itemRequestDtoInput, itemRequest.getRequester().getId())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", itemRequest.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"" + itemRequest.getDescription() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description", equalTo(itemRequest.getDescription())));

        verify(itemRequestService).saveRequest(any(ItemRequestDtoInput.class), anyLong());
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequest itemRequest = createItemRequest(1);
        itemRequest.setCreated(LocalDateTime.parse(itemRequest.getCreated().format(formatter)));
        ItemRequestOutput itemRequestOutput = ItemRequestOutput.builder()
                        .requester(itemRequest.getRequester())
                        .id(itemRequest.getId())
                        .description(itemRequest.getDescription())
                        .created(itemRequest.getCreated())
                        .build();

        when(itemRequestService.getRequestById(itemRequest.getId(), itemRequest.getRequester().getId())).thenReturn(itemRequestOutput);

        mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", itemRequest.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.description", equalTo(itemRequest.getDescription())));

        verify(itemRequestService).getRequestById(anyLong(), anyLong());
    }

    @Test
    void getRequests() throws Exception {
        ItemRequest itemRequest1 = createItemRequest(1);
        ItemRequest itemRequest2 = createItemRequest(2);
        itemRequest2.setRequester(itemRequest1.getRequester());
        ItemRequestOutput itemRequestOutput1 = ItemRequestOutput.builder()
                .requester(itemRequest1.getRequester())
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .created(itemRequest1.getCreated())
                .build();
        ItemRequestOutput itemRequestOutput2 = ItemRequestOutput.builder()
                .requester(itemRequest2.getRequester())
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .created(itemRequest2.getCreated())
                .build();
        List<ItemRequestOutput> expectedList = Arrays.asList(itemRequestOutput1, itemRequestOutput2);

        when(itemRequestService.getRequests(itemRequest1.getRequester().getId())).thenReturn(expectedList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", itemRequest1.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequest1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequest2.getId()))
                .andExpect(jsonPath("$[0].description", equalTo(itemRequest1.getDescription())))
                .andExpect(jsonPath("$[1].description", equalTo(itemRequest2.getDescription())))
                .andExpect(jsonPath("$[0].requester.id").value(itemRequest1.getRequester().getId()))
                .andExpect(jsonPath("$[1].requester.id").value(itemRequest2.getRequester().getId()));

        verify(itemRequestService).getRequests(anyLong());
    }

    @Test
    void testGetRequests() throws Exception {
        ItemRequest itemRequest1 = createItemRequest(1);
        ItemRequest itemRequest2 = createItemRequest(2);
        ItemRequestOutput itemRequestOutput1 = ItemRequestOutput.builder()
                .requester(itemRequest1.getRequester())
                .id(itemRequest1.getId())
                .description(itemRequest1.getDescription())
                .created(itemRequest1.getCreated())
                .build();
        ItemRequestOutput itemRequestOutput2 = ItemRequestOutput.builder()
                .requester(itemRequest2.getRequester())
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .created(itemRequest2.getCreated())
                .build();
        List<ItemRequestOutput> expectedList = Arrays.asList(itemRequestOutput1, itemRequestOutput2);

        when(itemRequestService.getAllRequests(0, 10,itemRequest1.getRequester().getId())).thenReturn(expectedList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", itemRequest1.getRequester().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequest1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemRequest2.getId()))
                .andExpect(jsonPath("$[0].description", equalTo(itemRequest1.getDescription())))
                .andExpect(jsonPath("$[1].description", equalTo(itemRequest2.getDescription())))
                .andExpect(jsonPath("$[0].requester.id").value(itemRequest1.getRequester().getId()))
                .andExpect(jsonPath("$[1].requester.id").value(itemRequest2.getRequester().getId()));

        verify(itemRequestService).getAllRequests(anyInt(), anyInt(),anyLong());
    }

    private ItemRequest createItemRequest(int num) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(createUser(num+3));
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
        comment.setAuthor(createUser(i+5));
        comment.setCreated(LocalDateTime.now().plusHours(1));
        comment.setId((long) i);
        comment.setText("Lorems" + i);
        return comment;
    }
}