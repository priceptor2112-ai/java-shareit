package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class ItemRequestDto {
    Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    String description;

    LocalDateTime created;

    List<ItemResponseDto> items;

    @Data
    @FieldDefaults(level = PRIVATE)
    public static class ItemResponseDto {
        Long id;
        String name;
        Long ownerId;
    }
}