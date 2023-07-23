package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

import javax.validation.ValidationException;
import java.util.Map;

@Service
public class BookingRemoteCommandImpl extends BaseClient implements BookingRemoteCommand {


    public BookingRemoteCommandImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Override
    public ResponseEntity<Object> addBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Время начала и конца бронирования не может совпадать");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Время конца бронирования не может быть раньше его начала");
        }
        return post("", userId, bookingDto);
    }

    @Override
    public ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    @Override
    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    @Override
    public ResponseEntity<Object> getAllByBookerId(Long bookerId, String state, Integer from, Integer size) {
        Map<String, Object> parameters;
        if (from != null && size != null) {
            parameters = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("?state={state}&from={from}&size={size}", bookerId, parameters);
        } else {
            parameters = Map.of(
                    "state", state
            );
            return get("?state={state}", bookerId, parameters);
        }
    }

    @Override
    public ResponseEntity<Object> getAllByBookerItems(Long ownerId, String state, Integer from, Integer size) {
        Map<String, Object> parameters;
        if (from != null && size != null) {
            parameters = Map.of(
                    "state", state,
                    "from", from,
                    "size", size
            );
            return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
        } else {
            parameters = Map.of(
                    "state", state
            );
            return get("/owner?state={state}", ownerId, parameters);
        }
    }
}

