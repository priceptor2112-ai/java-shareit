package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String ITEM_ID_PATH = "/{itemId}";

    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingsDto> getItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemsWithBookings(userId);
    }

    @GetMapping(ITEM_ID_PATH)
    public ItemWithBookingsDto getItemById(@PathVariable Long itemId,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemWithBookingsById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchAvailable(text);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(ITEM_ID_PATH)
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @PostMapping(ITEM_ID_PATH + "/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(USER_ID_HEADER) Long userId,
                                 @Valid @RequestBody CommentRequestDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}