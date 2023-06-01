package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    public void testItemToItemDtoWithBookings() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner 1");
        owner.setEmail("owner1@example.com");
        item.setOwner(owner);

        BookingIdAndBookerId lastBooking = new BookingIdAndBookerId(10L, 100L);
        BookingIdAndBookerId nextBooking = new BookingIdAndBookerId(20L, 200L);

        List<Comment> comments = new ArrayList<>();
        comments.add(createComment(1L, "Comment 1", item, LocalDateTime.now(), owner));
        comments.add(createComment(2L, "Comment 2", item, LocalDateTime.now(), owner));

        ItemDtoWithBookings itemDtoWithBookings = itemMapper.itemToItemDtoWithBookings(item, lastBooking, nextBooking, comments);

        assertEquals(item.getId(), itemDtoWithBookings.getId());
        assertEquals(item.getName(), itemDtoWithBookings.getName());
        assertEquals(item.getDescription(), itemDtoWithBookings.getDescription());
        assertEquals(item.getAvailable(), itemDtoWithBookings.getAvailable());
        assertEquals(owner.getId(), itemDtoWithBookings.getOwner().getId());
        assertEquals(owner.getName(), itemDtoWithBookings.getOwner().getName());
        assertEquals(owner.getEmail(), itemDtoWithBookings.getOwner().getEmail());
        assertEquals(lastBooking, itemDtoWithBookings.getLastBooking());
        assertEquals(nextBooking, itemDtoWithBookings.getNextBooking());
        assertEquals(comments.size(), itemDtoWithBookings.getComments().size());
    }

    @Test
    public void testItemDTOToItem() {
        ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings();
        itemDtoWithBookings.setId(1L);
        itemDtoWithBookings.setName("Item 1");
        itemDtoWithBookings.setDescription("Description 1");
        itemDtoWithBookings.setAvailable(true);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner 1");
        owner.setEmail("owner1@example.com");
        itemDtoWithBookings.setOwner(owner);

        Item item = itemMapper.itemDTOToItem(itemDtoWithBookings);

        assertEquals(itemDtoWithBookings.getId(), item.getId());
        assertEquals(itemDtoWithBookings.getName(), item.getName());
        assertEquals(itemDtoWithBookings.getDescription(), item.getDescription());
        assertEquals(itemDtoWithBookings.getAvailable(), item.getAvailable());
        assertEquals(owner.getId(), item.getOwner().getId());
        assertEquals(owner.getName(), item.getOwner().getName());
        assertEquals(owner.getEmail(), item.getOwner().getEmail());
    }

    @Test
    public void testItemToItemDtoDefault() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setDescription("Description 1");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner 1");
        owner.setEmail("owner1@example.com");
        item.setOwner(owner);

        ItemDtoDefault itemDtoDefault = itemMapper.itemToItemDtoDefault(item);

        assertEquals(item.getId(), itemDtoDefault.getId());
        assertEquals(item.getName(), itemDtoDefault.getName());
        assertEquals(item.getDescription(), itemDtoDefault.getDescription());
        assertEquals(item.getAvailable(), itemDtoDefault.getAvailable());
        assertEquals(owner.getId(), itemDtoDefault.getOwner().getId());
        assertEquals(owner.getName(), itemDtoDefault.getOwner().getName());
        assertEquals(owner.getEmail(), itemDtoDefault.getOwner().getEmail());
    }

    private Comment createComment(Long id, String text, Item item, LocalDateTime created, User author) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setCreated(created);
        comment.setAuthor(author);
        return comment;
    }
}
