package ru.practicum.shareit.exceptions;

public class WrongBookingTimeException extends RuntimeException {
    public WrongBookingTimeException(String message) {
        super(message);
    }
}
