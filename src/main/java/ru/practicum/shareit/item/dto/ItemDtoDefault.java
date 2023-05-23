package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
@Data
public class ItemDtoDefault {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
