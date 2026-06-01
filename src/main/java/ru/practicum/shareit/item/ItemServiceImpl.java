package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    public List<ItemWithBookingsDto> getItemsWithBookings(Long userId) {
        validateUser(userId);
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        return items.stream()
                .map(item -> toItemWithBookingsDto(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemWithBookingsDto getItemWithBookingsById(Long itemId, Long userId) {
        validateUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена с id: " + itemId));
        return toItemWithBookingsDto(item, userId);
    }

    private ItemWithBookingsDto toItemWithBookingsDto(Item item, Long userId) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (item.getOwnerId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(item.getId(), now, BookingStatus.APPROVED)
                    .orElse(null);
            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), now, BookingStatus.APPROVED)
                    .orElse(null);
            dto.setLastBooking(bookingMapper.toShortDto(lastBooking));
            dto.setNextBooking(bookingMapper.toShortDto(nextBooking));
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());
        dto.setComments(comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        validateUser(ownerId);
        Item item = itemMapper.toEntity(itemDto, ownerId);
        item = itemRepository.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        validateUser(ownerId);
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена с id: " + itemId));

        if (!existing.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existing.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existing.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existing.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(existing);
        return itemMapper.toDto(existing);
    }

    @Override
    public List<ItemDto> searchAvailable(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailable(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentRequestDto commentDto) {
        validateUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена с id: " + itemId));

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (!hasBooked) {
            throw new RuntimeException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = commentMapper.toEntity(itemId, userId, commentDto.getText());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден с id: " + userId);
        }
    }
}