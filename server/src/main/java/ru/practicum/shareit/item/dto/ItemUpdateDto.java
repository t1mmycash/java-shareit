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

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<Boolean> getAvailable() {
        return Optional.ofNullable(available);
    }
}
