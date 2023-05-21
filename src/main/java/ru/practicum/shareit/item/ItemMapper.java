package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingIdAndBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    CommentRepository commentRepository;

    @Autowired
    public ItemMapper(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public ItemDto itemToItemDTO(Item item, BookingIdAndBookerId last, BookingIdAndBookerId next) {
        if (item == null) {
            return null;
        }
        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream().map(CommentMapper::commentToCommentDto).collect(Collectors.toList());
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
        itemDto.setComments(comments);
        itemDto.setLastBooking(last);
        itemDto.setNextBooking(next);
        return itemDto;
    }

    public Item itemDTOToItem(ItemDto itemDTO) {
        if (itemDTO == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDTO.getId());
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setAvailable(itemDTO.getAvailable());
        item.setOwner(itemDTO.getOwner());
        return item;
    }
}
