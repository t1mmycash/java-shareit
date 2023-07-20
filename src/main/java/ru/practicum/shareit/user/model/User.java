package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.Constant;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = Constant.NAME_NOT_BLANK)
    private String name;
    @Email(message = Constant.EMAIL_NOT_VALID)
    @NotBlank(message = Constant.EMAIL_NOT_BLANK)
    @Column(unique = true)
    private String email;

}
