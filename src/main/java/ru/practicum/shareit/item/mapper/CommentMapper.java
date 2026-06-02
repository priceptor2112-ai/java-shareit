package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        User author = userRepository.findById(comment.getAuthorId()).orElse(null);
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                author != null ? author.getName() : null,
                comment.getCreated()
        );
    }

    public Comment toEntity(Long itemId, Long authorId, String text) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setText(text);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}