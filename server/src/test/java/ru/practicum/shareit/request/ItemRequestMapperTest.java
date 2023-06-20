package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void itemRequestDtoInputToItemRequest() {
        ItemRequest expectedItem = createItemRequest(1);
        expectedItem.setCreated(LocalDateTime.parse(expectedItem.getCreated().format(formatter)));
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput();
        itemRequestDtoInput.setDescription(expectedItem.getDescription());
        ItemRequest actualItem = itemRequestMapper.itemRequestDtoInputToItemRequest(itemRequestDtoInput, expectedItem.getRequester());
        actualItem.setId(expectedItem.getId());
        actualItem.setCreated(expectedItem.getCreated());

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(expectedItem.getDescription(), actualItem.getDescription());
        assertEquals(expectedItem.getRequester(), actualItem.getRequester());
        assertEquals(expectedItem.getCreated(), actualItem.getCreated());
    }

    @Test
    void itemRequestToItemRequestOutput() {
        ItemRequest itemRequest = createItemRequest(1);
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
        ItemRequestOutput expectedItem = ItemRequestOutput.builder()
                .requester(itemRequest.getRequester())
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();

        ItemRequestOutput actualItem = itemRequestMapper.itemRequestToItemRequestOutput(itemRequest, null);

        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(expectedItem.getDescription(), actualItem.getDescription());
        assertEquals(expectedItem.getRequester(), actualItem.getRequester());
        assertEquals(expectedItem.getCreated(), actualItem.getCreated());
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