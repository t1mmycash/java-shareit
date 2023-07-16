package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentMapperTest {
    @Autowired
    private CommentMapper mapper;
    private final LocalDateTime created = LocalDateTime.now();

    @Test
    void toCommentResponseDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .created(created)
                .author(User.builder()
                        .name("name")
                        .build())
                .build();

        CommentResponseDto resultComment = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .created(created)
                .authorName("name")
                .build();

        CommentResponseDto result = mapper.toCommentResponseDto(comment);

        assertEquals(resultComment, result);
    }
}