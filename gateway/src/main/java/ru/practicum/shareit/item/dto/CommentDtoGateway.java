package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDtoGateway {
    @NotNull
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
