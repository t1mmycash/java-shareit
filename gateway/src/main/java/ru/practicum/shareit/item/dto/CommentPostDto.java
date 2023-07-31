package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentPostDto {

    @NotNull(message = "Текст комментария не может быть null")
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
}
