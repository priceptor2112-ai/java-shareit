package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder.build());
    }

    public ResponseEntity<Object> create(Long userId, BookingRequestDto requestDto) {
        return post(API_PREFIX, userId, requestDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(API_PREFIX + "/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(API_PREFIX + "?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByOwner(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get(API_PREFIX + "/owner?state={state}", userId, parameters);
    }
}