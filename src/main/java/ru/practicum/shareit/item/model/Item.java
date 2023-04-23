package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Data
public class Item {
    @Positive
    private Long id;
    @Max(50)
    private String name;
    @Max(200)
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public boolean isAvailable() {
        return available;
    }
}
