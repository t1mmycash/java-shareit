package ru.practicum.shareit.request.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.RequestPostDto;

public interface RequestRemoteCommand {
    ResponseEntity<Object> createRequest(RequestPostDto requestDto, Long userId);

    ResponseEntity<Object> getAllOwnRequests(Long userId);

    ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size);

    ResponseEntity<Object> getRequestById(Long userId, Long requestId);
}
