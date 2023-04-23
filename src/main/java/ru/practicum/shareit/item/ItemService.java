package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return null;
    }

    public Item getItemById(Long id) {
        return itemRepository.getItemById(id);
    }

    public Item createItem(Item item, Long userId) {
        return itemRepository.create(item, userId);
    }

    public Item updateItem(Long id, Map<String, Object> fields, Long userId) {
        return  itemRepository.updateItem(id, fields, userId);
    }

    public void deleteItem(Long id) {
    }

    public List<Item> getItemsByUser(Long userId) {
        return itemRepository.getAllItemOneOwner(userId);
    }

    public List<Item> search(String text) {
        return itemRepository.search(text);
    }
}
