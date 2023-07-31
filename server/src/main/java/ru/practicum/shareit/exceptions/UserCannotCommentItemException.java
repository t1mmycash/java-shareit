package ru.practicum.shareit.exceptions;

public class UserCannotCommentItemException extends RuntimeException {
    public UserCannotCommentItemException(String message) {
        super(message);
    }
}
