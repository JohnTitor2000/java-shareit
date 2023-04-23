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
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // GET запрос для получения списка всех Item

    // GET запрос для получения Item по id
    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam(value = "text", defaultValue = "") String text) {
        return itemService.search(text);
    }

    // POST запрос для создания нового Item
    @PostMapping
    public Item createItem(@RequestBody Item item, @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.createItem(item, userId);
    }

    // PATCH запрос для обновления существующего Item
    @PatchMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Map<String, Object> fields,
                           @RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.updateItem(id, fields, userId);
    }

    // DELETE запрос для удаления Item по id
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @GetMapping
    public List<Item> getItemsByUser(@RequestHeader(name = "X-Sharer-User-Id", required = true) Long userId) {
        return  itemService.getItemsByUser(userId);
    }
}