package ru.practicum.shareit.request.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestPostDto;
import ru.practicum.shareit.request.service.RequestRemoteCommand;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class ItemRequestController {


    private final RequestRemoteCommand command;

    @PostMapping
    public HttpEntity<Object> createRequest(@RequestBody @Valid RequestPostDto requestDto,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Received a POST request for the endpoint /requests with userId_{}", userId);
        return command.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Received a GET request for the endpoint /requests with userId_{}", userId);
        return command.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PositiveOrZero(message = FROM_MUST_BE_POSITIVE_OR_ZERO) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive(message = SIZE_MUST_BE_POSITIVE) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received a GET request for the endpoint /requests/all with userId_{}", userId);
        return command.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Received a GET request for the endpoint /requests/{requestId} with userId_{}", userId);
        return command.getRequestById(userId, requestId);
    }
}
