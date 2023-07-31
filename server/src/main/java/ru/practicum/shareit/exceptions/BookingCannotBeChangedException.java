package ru.practicum.shareit.exceptions;

public class BookingCannotBeChangedException extends RuntimeException {
    public BookingCannotBeChangedException(String message) {
        super(message);
    }
}
