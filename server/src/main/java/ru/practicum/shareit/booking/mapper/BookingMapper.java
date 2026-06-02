package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        Item item = itemRepository.findById(booking.getItemId()).orElse(null);
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                item != null ? item.getName() : null,
                booking.getBookerId(),
                booking.getStatus().name()
        );
    }

    // Добавить этот метод
    public BookingShortDto toShortDto(Booking booking) {
        if (booking == null) return null;
        return new BookingShortDto(booking.getId(), booking.getBookerId());
    }
}