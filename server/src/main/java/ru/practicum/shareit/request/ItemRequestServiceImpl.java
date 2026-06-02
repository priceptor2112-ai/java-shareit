package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(String.format("Пользователь не найден с id: %d", userId));
        }
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        validateUser(userId);
        ItemRequest request = requestMapper.toEntity(requestDto, userId);
        request = requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getByUser(Long userId) {
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return requestRepository.findByRequestorId(userId, sort).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return requestRepository.findByRequestorIdNot(userId, sort).stream()
                .skip(from)
                .limit(size)
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        validateUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException(String.format("Запрос не найден с id: %d", requestId)));
        return requestMapper.toDto(request);
    }
}