package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Component
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        userStorage.emailCheck(user.getEmail());
        return userStorage.addUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long userId) {
        userStorage.userExistenceCheck(userId);
        return userStorage.getUserById(userId);
    }

    public User updateUser(long userId, UserUpdateDto changes) {
        userStorage.userExistenceCheck(userId);
        return userStorage.updateUser(userId, changes);
    }

    public void deleteUser(long userId) {
        userStorage.userExistenceCheck(userId);
        userStorage.deleteUser(userId);
    }
}
