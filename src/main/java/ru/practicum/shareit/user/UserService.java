package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.LackOfInformationException;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user.getEmail() == null || user.getName() == null) {
            System.out.println("до метода в сервисе дошло");
            throw new LackOfInformationException("Заполнены не все поля");
        }
        if (user.getEmail().isBlank() || user.getName().isBlank()) {
            throw new LackOfInformationException("Заполнены не все поля");
        }
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

    public User updateUser(long userId, Map<String, Object> params) {
        userStorage.userExistenceCheck(userId);
        return userStorage.updateUser(userId, params);
    }

    public void deleteUser(long userId) {
        userStorage.userExistenceCheck(userId);
        userStorage.deleteUser(userId);
    }
}
