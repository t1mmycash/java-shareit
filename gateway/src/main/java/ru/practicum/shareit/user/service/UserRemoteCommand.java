package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserRemoteCommand {
    ResponseEntity<Object> addUser(User userDto);

    ResponseEntity<Object> updateUser(Long userId, UserUpdateDto userDto);

    ResponseEntity<Object> getUserById(Long userId);

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> deleteUserById(Long userId);
}
