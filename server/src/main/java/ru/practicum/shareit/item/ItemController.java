package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoDefault;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComments(@PathVariable Long itemId, @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId, @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBookings getItemById(@PathVariable Long id, @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoDefault> search(@RequestParam(value = "text", defaultValue = "") String text) {
        return itemService.search(text);
    }

    @PostMapping
    public ItemDtoDefault createItem(@RequestBody Item item, @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDtoDefault updateItem(@PathVariable Long id, @RequestBody ItemDtoWithBookings itemDTOWithBookings,
                           @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.updateItem(id, itemDTOWithBookings, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getItemsByUser(@RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return  itemService.getItemsByUser(userId);
    }
}