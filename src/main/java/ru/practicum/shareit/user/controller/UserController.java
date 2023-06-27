package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Validated
public class UserController {
    private final String USER_ID_NOT_NULL = "id пользователя не может быть null";
    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(
            @PathVariable(value = "userId") @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @RequestBody @Valid UserUpdateDto changes) {
        return userService.updateUser(userId, changes);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(
            @PathVariable(value = "userId") @NotNull(message = USER_ID_NOT_NULL) Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
            @PathVariable(value = "userId") @NotNull(message = USER_ID_NOT_NULL) Long userId) {
        userService.deleteUser(userId);
    }

}
