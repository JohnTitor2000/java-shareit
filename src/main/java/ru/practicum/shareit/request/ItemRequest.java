package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    @Positive
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
