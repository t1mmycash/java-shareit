package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemRemoteCommand;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {


    private final ItemRemoteCommand command;

    @PostMapping
    public HttpEntity<Object> addItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                      @RequestBody @Valid ItemPostDto itemDto) {
        log.info("Received a POST request for the endpoint /items with userId_{}", ownerId);
        return command.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemUpdateDto itemDto) {
        log.info("Received a PATCH request for the endpoint /items/{itemId} with userId_{}", userId);
        return command.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Received a GET request for the endpoint /items/{itemId} with userId_{}", userId);
        return command.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                    @PositiveOrZero(message = FROM_MUST_BE_POSITIVE_OR_ZERO) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive(message = SIZE_MUST_BE_POSITIVE) @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received a GET request for the endpoint /items with userId_{}", ownerId);
        return command.getItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByNameOrDescription(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                                 @RequestParam String text,
                                                                 @PositiveOrZero(message = FROM_MUST_BE_POSITIVE_OR_ZERO) @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                 @Positive(message = SIZE_MUST_BE_POSITIVE) @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Received a GET request for the endpoint /items/search");
        return command.searchItemsByNameOrDescription(text, from, size, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @RequestBody @Valid CommentPostDto comment,
                                             @PathVariable Long itemId) {
        log.info("Received a POST request for the endpoint /items/{itemId}/comment with userId_{}", userId);
        return command.addComment(userId, comment, itemId);
    }
}

