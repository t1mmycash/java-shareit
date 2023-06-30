package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User addUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException(
                    String.format("Уже существует пользователь с email - %s", user.getEmail()));
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("Пользователя с id = %d не существует", userId)));
    }

    public User updateUser(long userId, UserUpdateDto changes) {
        User user = getUserById(userId);
        if (changes.getEmail().isPresent()) {
            user.setEmail(changes.getEmail().get());
        }
        if (changes.getName().isPresent()) {
            user.setName(changes.getName().get());
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException(
                    String.format("Уже существует пользователь с email - %s", user.getEmail()));
        }
    }

    public void deleteUser(long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(
                    String.format("Пользователя с id = %d не существует", userId));
        }
    }
}
