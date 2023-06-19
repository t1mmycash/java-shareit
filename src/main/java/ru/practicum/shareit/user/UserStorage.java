package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserStorage {
    private long newId = 1;
    private final Map<Long, User> users = new HashMap<>();

    public User addUser(User user) {
        user.setId(newId());
        users.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(long userId) {
        return users.get(userId);
    }

    public User updateUser(long userId, UserUpdateDto changes) {
        if (changes.getEmail().isPresent()) {
            emailForUpdateCheck(changes.getEmail().get(), userId);
            users.get(userId).setEmail(changes.getEmail().get());
        }
        if (changes.getName().isPresent()) {
            users.get(userId).setName(changes.getName().get());
        }
        return users.get(userId);
    }

    public void deleteUser(long userId) {
        users.remove(userId);
    }

    public void userExistenceCheck(long userId) {
        if (!users.containsKey(userId)) {
            String message = String.format("Пользователя с id = %d не существует", userId);
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    public void emailCheck(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                String message = String.format("Уже существует пользователь с email - %s", email);
                log.warn(message);
                throw new EmailAlreadyExistsException(message);
            }
        }
    }

    public void emailForUpdateCheck(String email, long userId) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && user.getId() != userId) {
                String message = String.format("Уже существует пользователь с email - %s", email);
                log.warn(message);
                throw new EmailAlreadyExistsException(message);
            }
        }
    }

    private long newId() {
        return newId++;
    }
}
