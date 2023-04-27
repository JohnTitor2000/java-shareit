package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collections;
import java.util.List;

@Service
public class ItemService {

    private ItemRepository itemRepository;
    private UserService userService;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public List<Item> getAllItems() {
        return null;
    }

    public Item getItemById(Long id) {
        return itemRepository.getItemById(id);
    }

    public Item createItem(Item item, Long userId) {
        if (userId != null && userService.getUserById(userId) == null) {
            throw new NotFoundException("User not found.");
        }
        if (item.getAvailable() == null || item.getName().isBlank() || item.getName() == null
                || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("The item status must be provided in the request.");
        }
        return itemRepository.create(item, userId);
    }

    public Item updateItem(Long id, ItemDto itemDTO, Long userId) {
        if (getItemById(id).equals(null)) {
            throw new NotFoundException("Item not found.");
        }
        if (!userId.equals(getItemById(id).getOwner().getId())) {
            throw new NotFoundException("You can't change owner of item");
        }
        return  itemRepository.updateItem(id, itemDTO, userId);
    }

    public void deleteItem(Long id) {
    }

    public List<Item> getItemsByUser(Long userId) {
        if (userService.getUserById(userId).equals(null)) {
            throw new NotFoundException("User not found");
        }
        return itemRepository.getAllItemOneOwner(userId);
    }

    public List<Item> search(String text) {
        if (text.isBlank()) {
            List<Item> empty = Collections.EMPTY_LIST;
            return empty;
        }
        return itemRepository.search(text);
    }
}
