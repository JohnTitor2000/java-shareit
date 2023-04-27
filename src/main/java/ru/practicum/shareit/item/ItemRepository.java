package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item, Long userId);

    List<Item> getAll();

    Item getItemById(Long id);

    void deleteItem(Long id);

    Item updateItem(Long id, ItemDto itemDTO, Long userId);

    List<Item> getAllItemOneOwner(Long userId);

    List<Item> search(String text);
}
