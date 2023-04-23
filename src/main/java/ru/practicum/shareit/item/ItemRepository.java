package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;

public interface ItemRepository {

    Item create(Item item, Long userId);

    List<Item> getAll();

    Item getItemById(Long id);

    void deleteItem(Long id);

    Item updateItem(Long id, Map<String, Object> fields, Long userId);

    List<Item> getAllItemOneOwner(Long userId);

    List<Item> search(String text);
}
