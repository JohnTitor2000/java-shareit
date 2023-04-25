package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Long, Item> itemData = new HashMap<>();
    private Long id = 1L;
    private UserRepository userRepository;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item create(Item item, Long userId) {
        item.setOwner(userRepository.findUser(userId));
        item.setId(getNextId());
        itemData.put(item.getId(), item);
        return  itemData.get(item.getId());
    }

    @Override
    public List<Item> getAll() {
        return itemData.values().stream().collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        return itemData.get(id);
    }

    @Override
    public Item updateItem(Long id, ItemDTO itemDTO, Long userId) {
        Item item = itemData.get(id);
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        return item;
    }

    @Override
    public List<Item> getAllItemOneOwner(Long userId) {
        return itemData.values().stream().filter(o -> o.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
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
