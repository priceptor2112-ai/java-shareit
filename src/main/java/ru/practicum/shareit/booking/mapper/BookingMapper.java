package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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

    public Booking toEntity(BookingRequestDto dto, Long bookerId) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItemId(dto.getItemId());
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public BookingShortDto toShortDto(Booking booking) {
        if (booking == null) return null;
        return new BookingShortDto(booking.getId(), booking.getBookerId());
    }
}