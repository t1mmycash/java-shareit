package ru.practicum.shareit.exceptions;

public class LackOfInformationException extends RuntimeException {
    public LackOfInformationException(String message) {
        super(message);
    }
}
