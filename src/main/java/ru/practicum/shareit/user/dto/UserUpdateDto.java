package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import java.util.Optional;

public class UserUpdateDto {
    private String name;
    @Email
    private String email;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
}
