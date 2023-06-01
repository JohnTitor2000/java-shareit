package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestOutput;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest itemRequestDtoInputToItemRequest(ItemRequestDtoInput itemRequestDtoInput, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(itemRequestDtoInput.getDescription());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public ItemRequestOutput itemRequestToItemRequestOutput(ItemRequest itemRequest, List<Item> items) {
        ItemRequestOutput itemRequestOutput = ItemRequestOutput.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
        return itemRequestOutput;
    }


}
