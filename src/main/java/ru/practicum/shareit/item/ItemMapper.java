package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDtoWithBookings itemToItemDtoWithBookings(Item item, BookingIdAndBookerId last, BookingIdAndBookerId next, List<Comment> comment) {
        CommentMapper commentMapper = new CommentMapper();
        if (item == null) {
            return null;
        }
        List<CommentDto> comments = comment.stream().map(commentMapper::commentToCommentDto).collect(Collectors.toList());
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

    public Item itemDTOToItem(ItemDtoWithBookings itemDTOWithBookings) {
        if (itemDTOWithBookings == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDTOWithBookings.getId());
        item.setName(itemDTOWithBookings.getName());
        item.setDescription(itemDTOWithBookings.getDescription());
        item.setAvailable(itemDTOWithBookings.getAvailable());
        item.setOwner(itemDTOWithBookings.getOwner());
        return item;
    }

    public ItemDtoDefault itemToItemDtoDefault(Item item) {
        ItemDtoDefault itemDtoDefault = new ItemDtoDefault();
        itemDtoDefault.setId(item.getId());
        itemDtoDefault.setName(item.getName());
        itemDtoDefault.setDescription(item.getDescription());
        itemDtoDefault.setOwner(item.getOwner());
        itemDtoDefault.setAvailable(item.getAvailable());
        itemDtoDefault.setRequestId(item.getRequestId());
        return itemDtoDefault;
    }
}
