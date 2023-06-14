package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetItemsByUser() throws Exception {
        Long userId = 1L;
        List<ItemDtoWithBookings> expectedItems = Arrays.asList(new ItemDtoWithBookings(), new ItemDtoWithBookings());

        when(itemService.getItemsByUser(userId)).thenReturn(expectedItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(itemService).getItemsByUser(userId);
    }

    @Test
    public void testGetItemById() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@email.com");

        CommentDto comment1 = new CommentDto();
        comment1.setId(1L);
        comment1.setText("Comment 1");
        comment1.setAuthorName("Author 1");

        CommentDto comment2 = new CommentDto();
        comment2.setId(2L);
        comment2.setText("Comment 2");
        comment2.setAuthorName("Author 2");

        List<CommentDto> comments = Arrays.asList(comment1, comment2);

        ItemDtoWithBookings expectedItem = new ItemDtoWithBookings();
        expectedItem.setId(itemId);
        expectedItem.setName("Item 1");
        expectedItem.setDescription("Description 1");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(user);
        expectedItem.setComments(comments);

        when(itemService.getItemById(itemId, userId)).thenReturn(expectedItem);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", equalTo(itemId.intValue())))
                        .andExpect(jsonPath("$.name", equalTo("Item 1")))
                        .andExpect(jsonPath("$.description", equalTo("Description 1")))
                        .andExpect(jsonPath("$.available", equalTo(true)))
                        .andExpect(jsonPath("$.owner.id", equalTo(userId.intValue())))
                        .andExpect(jsonPath("$.owner.name", equalTo(user.getName())))
                        .andExpect(jsonPath("$.owner.email", equalTo(user.getEmail())))
                        .andExpect(jsonPath("$.comments[0].id", equalTo(comment1.getId().intValue())))
                        .andExpect(jsonPath("$.comments[0].text", equalTo(comment1.getText())))
                        .andExpect(jsonPath("$.comments[0].authorName", equalTo(comment1.getAuthorName())))
                        .andExpect(jsonPath("$.comments[1].id", equalTo(comment2.getId().intValue())))
                        .andExpect(jsonPath("$.comments[1].authorName", equalTo(comment2.getAuthorName())))
                        .andExpect(jsonPath("$.comments[1].text", equalTo(comment2.getText())));
        verify(itemService).getItemById(itemId, userId);
    }

    @Test
    public void testSearch() throws Exception {
        String searchText = "example";

        Long itemId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@email.com");

        ItemDtoDefault expectedItem = new ItemDtoDefault();
        expectedItem.setId(itemId);
        expectedItem.setName("Item 1");
        expectedItem.setDescription("example");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(user);

        List<ItemDtoDefault> expectedItems = Collections.singletonList(expectedItem);

        when(itemService.search(searchText)).thenReturn(expectedItems);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(itemId.intValue())))
                .andExpect(jsonPath("$[0].name", equalTo(expectedItem.getName())))
                .andExpect(jsonPath("$[0].description", equalTo(expectedItem.getDescription())))
                .andExpect(jsonPath("$[0].available", equalTo(true)))
                .andExpect(jsonPath("$[0].owner.id", equalTo(userId.intValue())))
                .andExpect(jsonPath("$[0].owner.name", equalTo(user.getName())))
                .andExpect(jsonPath("$[0].owner.email", equalTo(user.getEmail())));
        verify(itemService).search(searchText);
    }

    @Test
    public void testCreateItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@email.com");
        ItemDtoDefault expectedItem = new ItemDtoDefault();
        expectedItem.setId(itemId);
        expectedItem.setName("Item 1");
        expectedItem.setDescription("example");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(user);

        when(itemService.createItem(any(Item.class), eq(userId))).thenReturn(expectedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"name\": \"Item 1\",\n" +
                                "    \"description\": \"Description 1\",\n" +
                                "    \"available\": true\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(itemId.intValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedItem.getName())))
                .andExpect(jsonPath("$.description", equalTo(expectedItem.getDescription())))
                .andExpect(jsonPath("$.available", equalTo(true)))
                .andExpect(jsonPath("$.owner.id", equalTo(userId.intValue())))
                .andExpect(jsonPath("$.owner.name", equalTo(user.getName())))
                .andExpect(jsonPath("$.owner.email", equalTo(user.getEmail())));

        verify(itemService).createItem(any(Item.class), eq(userId));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("johndoe@email.com");
        ItemDtoDefault expectedItem = new ItemDtoDefault();
        expectedItem.setId(itemId);
        expectedItem.setName("Item 1");
        expectedItem.setDescription("example");
        expectedItem.setAvailable(true);
        expectedItem.setOwner(user);

        when(itemService.updateItem(eq(itemId), any(ItemDtoWithBookings.class), eq(userId))).thenReturn(expectedItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(itemId.intValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedItem.getName())))
                .andExpect(jsonPath("$.description", equalTo(expectedItem.getDescription())))
                .andExpect(jsonPath("$.available", equalTo(true)))
                .andExpect(jsonPath("$.owner.id", equalTo(userId.intValue())))
                .andExpect(jsonPath("$.owner.name", equalTo(user.getName())))
                .andExpect(jsonPath("$.owner.email", equalTo(user.getEmail())));

        verify(itemService).updateItem(eq(itemId), any(ItemDtoWithBookings.class), eq(userId));
    }

    @Test
    public void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/{id}", 1L))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(1L);
    }

    @Test
    public void testAddComment() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment 1");

        when(itemService.addComment(eq(itemId), eq(userId), eq(commentDto))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"text\": \"Comment 1\"\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo(commentDto.getText())));

        verify(itemService).addComment(eq(itemId), eq(userId), eq(commentDto));
    }
}
