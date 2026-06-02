package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(serverUrl, builder.build());
    }

    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(API_PREFIX + "/" + userId, userId);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post(API_PREFIX, null, userDto);
    }

    public ResponseEntity<Object> update(Long userId, UserDto userDto) {
        return patch(API_PREFIX + "/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(API_PREFIX + "/" + userId, userId);
    }
}