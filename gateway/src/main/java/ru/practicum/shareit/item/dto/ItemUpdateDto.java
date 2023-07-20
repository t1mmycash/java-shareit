package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Optional;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemUpdateDto {
    private String name;
    private String description;
    private Boolean available;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }
}
