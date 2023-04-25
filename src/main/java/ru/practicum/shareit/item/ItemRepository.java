package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item, Long userId);

    List<Item> getAll();

    Item getItemById(Long id);

    void deleteItem(Long id);

    Item updateItem(Long id, ItemDTO itemDTO, Long userId);

    List<Item> getAllItemOneOwner(Long userId);

    List<Item> search(String text);
}
