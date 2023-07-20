package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum SortType {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<SortType> from(String stringState) {
        for (SortType state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
