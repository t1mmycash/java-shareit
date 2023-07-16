package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserResultDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserResultDto toUserResultDto(User user) {
        return new UserResultDto(user.getId(), user.getName(), user.getEmail());
    }
}
