package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoGateway;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsGateWay;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDtoGateway item,
                                             @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Create item with itemName={}, available={}, description={}, requestId={}, userId={}",
                item.getName(), item.getAvailable(), item.getDescription(), item.getRequestId(), userId);
        return itemClient.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@PathVariable @Positive Long itemId,
                                              @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId,
                                              @RequestBody @Valid CommentDtoGateway commentDtoGateway) {
        log.info("Add comment with itemId={}, userId={}, authorName={}, text={}",
                itemId, userId, commentDtoGateway.getAuthorName(), commentDtoGateway.getText());
        return itemClient.addComment(itemId, userId, commentDtoGateway);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id,
                                              @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Get item with itemId={}, userId={}", id, userId);
        return itemClient.getItemById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text", defaultValue = "") @NotBlank String text, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Search item with text={}, userId={}", text, userId);
        return itemClient.search(text, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long id, @RequestBody ItemDtoWithBookingsGateWay itemDTOWithBookingsGateWay,
                                            @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Update item with name={}, available={}, description={}, userId={}", itemDTOWithBookingsGateWay.getName(),
                itemDTOWithBookingsGateWay.getAvailable(), itemDTOWithBookingsGateWay.getDescription(), userId);
        return itemClient.updateItem(id, itemDTOWithBookingsGateWay, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive Long id, @RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Delete item with itemId={} by userId={}", id, userId);
        return itemClient.deleteItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(name = "X-Sharer-User-Id", required = true) @Positive Long userId) {
        log.info("Get items by user={}", userId);
        return  itemClient.getItemsByUser(userId);
    }
}
