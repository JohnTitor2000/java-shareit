package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class Booking {
    @Positive
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    @Pattern(regexp = "WAITING|APPROVED|REJECTED|CANCELED")
    String status;
}
