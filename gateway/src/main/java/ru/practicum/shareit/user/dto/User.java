package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.Constant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    @NotBlank(message = Constant.NAME_NOT_BLANK)
    private String name;
    @Email(message = Constant.EMAIL_NOT_VALID)
    @NotBlank(message = Constant.EMAIL_NOT_BLANK)
    private String email;
}
