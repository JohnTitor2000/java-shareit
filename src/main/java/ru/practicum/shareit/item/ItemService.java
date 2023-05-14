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
    private ItemMapper itemMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public List<Item> getAllItems() {
        return null;
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
    }

    public Item createItem(Item item, Long userId) {
        if (item.getAvailable() == null || item.getName().isBlank() || item.getName() == null
                || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("The item status must be provided in the request.");
        }
        if (userId != null && userService.getUserById(userId) == null) {
            throw new NotFoundException("User not found.");
        }
        item.setOwner(userService.getUserById(userId));
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, ItemDto itemDTO, Long userId) {
        getItemById(id);
        if (!userId.equals(getItemById(id).getOwner().getId())) {
            throw new NotFoundException("You can't change owner of item");
        }
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        return  itemRepository.save(item);
    }

    public void deleteItem(Long id) {
    }

    public List<Item> getItemsByUser(Long userId) {
        if (userService.getUserById(userId).equals(null)) {
            throw new NotFoundException("User not found");
        }
        return itemRepository.findByOwnerId(userId);
    }

    public List<Item> search(String text) {
        if (text.isBlank()) {
            List<Item> empty = Collections.EMPTY_LIST;
            return empty;
        }
        return itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text);
    }
}
