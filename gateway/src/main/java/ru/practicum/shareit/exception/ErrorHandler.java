package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleValidation(final ValidationException e) {
        log.warn("Ошибка валидации 400 {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> result = exception.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError ->
                                String.format("Ошибка Валидации '%s' значение = '%s'",
                                        fieldError.getField(), fieldError.getRejectedValue()),
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")));
        log.warn("Ошибка 400 {}, {}", result, exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.warn("Ошибка сервера 500 {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> exc(ConstraintViolationException e) {
        log.warn("Ошибка валидации 400 {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        String message = exception.getMessage();
        Map<String, String> result = Map.of("Ошибка Request", Objects.isNull(message) ? "Неизвестно" : message);
        log.warn("Ошибка 400 {}, {}", result, exception);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        final String error = String.format("Unknown %s: %s", e.getName(), e.getValue());
        log.warn("Ошибка 400 Unknown {}: {}", e.getName(), e.getValue());
        return new ErrorResponse(error);
    }
}
