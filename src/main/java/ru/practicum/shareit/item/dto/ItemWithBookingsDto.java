package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ItemWithBookingsDto extends ItemDto {
    BookingShortDto lastBooking;
    BookingShortDto nextBooking;
    List<CommentDto> comments;
}