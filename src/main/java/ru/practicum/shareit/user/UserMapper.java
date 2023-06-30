package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserResultDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserResultDto toUserResultDto(User user) {
        return new UserResultDto(user.getId(), user.getName(), user.getEmail());
    }
}
