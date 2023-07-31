package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserResultDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper mapper;

    @Test
    void toUserResultDto() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();

        UserResultDto resultUser = UserResultDto.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();

        UserResultDto result = mapper.toUserResultDto(user);

        assertEquals(resultUser, result);
    }
}