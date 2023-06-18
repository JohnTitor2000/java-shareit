package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsGateWay;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/items")
public class ItemController {

    ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDtoGateway item, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return itemClient.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@PathVariable @Positive Long itemId, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId, @RequestBody @Valid CommentDtoGateway commentDtoGateway) {
        return itemClient.addComment(itemId, userId, commentDtoGateway);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text", defaultValue = "") @NotBlank String text, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return itemClient.search(text, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long id, @RequestBody ItemDtoWithBookingsGateWay itemDTOWithBookingsGateWay,
                                     @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return itemClient.updateItem(id, itemDTOWithBookingsGateWay, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive Long id, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return itemClient.deleteItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        return  itemClient.getItemsByUser(userId);
    }
}
