package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import java.util.List;

public interface ItemService {
    List<ItemWithBookingsDto> getItemsWithBookings(Long userId);
    ItemWithBookingsDto getItemWithBookingsById(Long itemId, Long userId);
    ItemDto createItem(Long ownerId, ItemDto itemDto);
    ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto);
    List<ItemDto> searchAvailable(String text);
    CommentDto addComment(Long itemId, Long userId, String text);
}