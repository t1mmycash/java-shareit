package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserRemoteCommandImpl;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRemoteCommandImpl command;


    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid User userDto) {
        log.info("Received a POST request for the endpoint /users");
        return command.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Valid UserUpdateDto userDto) {
        log.info("Received a PATCH request for the endpoint /users/{userId} with userId_{}", userId);
        return command.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Received a GET request for the endpoint /users/{userId} with userId_{}", userId);
        return command.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Received a GET request for the endpoint /users");
        return command.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Received a DELETE request for the endpoint /users/{userId} with userId_{}", userId);
        command.deleteUserById(userId);
    }
}
