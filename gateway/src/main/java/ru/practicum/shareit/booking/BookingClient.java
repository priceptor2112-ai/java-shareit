package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;  // ← этот импорт должен быть
import ru.practicum.shareit.booking.dto.BookingRequestDto;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder.build());
    }

    public ResponseEntity<Object> create(Long userId, BookingRequestDto requestDto) {
        return post(API_PREFIX, userId, requestDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        // Исправлено: убрали третий аргумент, так как patch ожидает только path и userId
        String path = API_PREFIX + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state) {
        return get(API_PREFIX + "?state=" + state, userId);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state) {
        return get(API_PREFIX + "/owner?state=" + state, userId);
    }
}