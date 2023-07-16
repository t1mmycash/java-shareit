package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void addUser_whenInvoked_thenUserWillBeSavedAndReturn() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userService.addUser(user);

        assertEquals(user, result);
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void addUser_whenEmailAlreadyExists_thenExceptionWillBeThrown() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        when(userRepository.saveAndFlush(user)).thenThrow(new DataIntegrityViolationException(""));

        EmailAlreadyExistsException e = assertThrows(
                EmailAlreadyExistsException.class, () -> userService.addUser(user));
        assertEquals("Уже существует пользователь с email - email@gmail.com", e.getMessage());
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnUsersList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenNoUsers_thenReturnEmptyList() {
        List<User> users = Collections.emptyList();
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_whenInvoked_thenReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    void updateUser_whenNameAndEmailAreUpdated_thenReturnUpdatedUser() {
        UserUpdateDto changes = UserUpdateDto.builder()
                .name("updatedName")
                .email("updatedEmail@gmail.com")
                .build();
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .name("updatedName")
                .email("updatedEmail@gmail.com")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, changes);

        assertEquals(updatedUser, result);
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(userRepository, times(1)).saveAndFlush(updatedUser);
    }

    @Test
    void updateUser_whenNameIsUpdated_thenReturnUpdatedUser() {
        UserUpdateDto changes = UserUpdateDto.builder()
                .name("updatedName")
                .build();
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .name("updatedName")
                .email("email@gmail.com")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, changes);

        assertEquals(updatedUser, result);
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(userRepository, times(1)).saveAndFlush(updatedUser);
    }

    @Test
    void updateUser_whenEmailIsUpdated_thenReturnUpdatedUser() {
        UserUpdateDto changes = UserUpdateDto.builder()
                .email("updatedEmail@gmail.com")
                .build();
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .name("name")
                .email("updatedEmail@gmail.com")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateUser(1L, changes);

        assertEquals(updatedUser, result);
        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(userRepository, times(1)).saveAndFlush(updatedUser);
    }

    @Test
    void updateUser_whenEmailAlreadyExists() {
        UserUpdateDto changes = UserUpdateDto.builder()
                .email("updatedEmail@gmail.com")
                .build();
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();
        User updatedUser = User.builder()
                .id(1L)
                .name("name")
                .email("updatedEmail@gmail.com")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(updatedUser)).thenThrow(new DataIntegrityViolationException(""));

        EmailAlreadyExistsException e = assertThrows(
                EmailAlreadyExistsException.class, () -> userService.updateUser(1L, changes));

        assertEquals("Уже существует пользователь с email - updatedEmail@gmail.com", e.getMessage());
    }

    @Test
    void deleteUer_whenInvoked_thenRepositoryDeleteMethodWillBeCalled() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_whenUserNotFound_thenExceptionWillBeThrown() {
        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(1L);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> userService.deleteUser(1L));
        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
    }
}