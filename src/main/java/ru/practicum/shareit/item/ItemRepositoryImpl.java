package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exaption.BadRequestException;
import ru.practicum.shareit.exaption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Long, Item> itemData = new HashMap<>();
    private Long id = Long.valueOf(1);
    private UserRepository userRepository;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item create(Item item, Long userId) {
        if (userId != null && userRepository.findUser(userId) == null) {
            throw new NotFoundException("User not found.");
        } else {
            item.setOwner(userRepository.findUser(userId));
        }
        if (item.getAvailable() == null || item.getName().isBlank() || item.getName() == null
                || item.getDescription() == null || item.getDescription().isBlank()) {
            throw new BadRequestException("The item status must be provided in the request.");
        }
        item.setId(getNextId());
        itemData.put(item.getId(), item);
        return  itemData.get(item.getId());
    }

    @Override
    public List<Item> getAll() {
        return null;
    }

    @Override
    public Item getItemById(Long id) {
        return itemData.get(id);
    }

    @Override
    public Item updateItem(Long id, Map<String, Object> fields, Long userId) {
        Item item = itemData.get(id);
        if (item.equals(null)) {
            throw new NotFoundException("Item not found.");
        }
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("You can't change owner of item");
        }
        if (fields.containsKey("name")) {
            item.setName(String.valueOf(fields.get("name")));
        }
        if (fields.containsKey("description")) {
            item.setDescription(String.valueOf(fields.get("description")));
        }
        if (fields.containsKey("available")) {
            item.setAvailable((Boolean) fields.get("available"));
        }
        itemData.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllItemOneOwner(Long userId) {
        if (userRepository.findUser(userId).equals(null)) {
            throw new NotFoundException("");
        }
        return itemData.values().stream().filter(o -> o.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            List<Item> empty = Collections.EMPTY_LIST;
            return empty;
        }
        return itemData.values().stream().filter(o -> o.getDescription().toLowerCase().contains(text.toLowerCase()) && o.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        itemData.remove(id);
    }

    private Long getNextId() {
        return id++;
    }
}
