package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(String.format("Пользователь не найден с id: %d", userId));
        }
    }

    @Override
    public BookingDto create(Long userId, BookingRequestDto requestDto) {
        validateUser(userId);
        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));

        if (item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Нельзя бронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new RuntimeException("Вещь недоступна для бронирования");
        }

        if (requestDto.getEnd().isBefore(requestDto.getStart()) || requestDto.getEnd().equals(requestDto.getStart())) {
            throw new RuntimeException("Дата окончания должна быть позже даты начала");
        }

        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setItemId(requestDto.getItemId());
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        validateUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Бронирование не найдено с id: %d", bookingId)));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));

        if (!item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Подтвердить бронирование может только владелец вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        validateUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(String.format("Бронирование не найдено с id: %d", bookingId)));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));

        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Просмотр бронирования доступен только автору или владельцу вещи");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(userId, now, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByBookerId(userId, now, sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByBookerId(userId, now, sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new RuntimeException(String.format("Unknown state: %s", state));
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long userId, String state) {
        validateUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerId(userId, sort);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByItemOwnerId(userId, now, sort);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByItemOwnerId(userId, now, sort);
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByItemOwnerId(userId, now, sort);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new RuntimeException(String.format("Unknown state: %s", state));
        }

        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}