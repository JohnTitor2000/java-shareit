package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    @Positive
    private Long id;
    @Max(50)
    private String name;
    @Max(200)
    private String description;
    private Boolean available;
    private ItemRequest request;
    private User owner;
}
