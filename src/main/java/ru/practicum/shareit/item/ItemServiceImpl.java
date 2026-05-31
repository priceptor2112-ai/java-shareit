package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;  // ← Правильный импорт
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        validateUser(ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        validateUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена с id: " + itemId));
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        validateUser(ownerId);

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new RuntimeException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new RuntimeException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new RuntimeException("Статус доступности должен быть указан");
        }

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

        itemRepository.update(existing);
        return itemMapper.toDto(existing);
    }

    @Override
    public List<ItemDto> searchAvailable(String text) {
        return itemRepository.searchAvailable(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден с id: " + userId);
        }
    }
}