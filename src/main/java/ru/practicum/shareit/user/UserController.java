package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable(value = "userId") long userId,
                           @RequestBody @Valid UserUpdateDto changes) {
        return userService.updateUser(userId, changes);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable(value = "userId") long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(value = "userId") long userId) {
        userService.deleteUser(userId);
    }

}
