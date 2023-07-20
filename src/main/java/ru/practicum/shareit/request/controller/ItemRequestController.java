package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestBody @Valid ItemRequest itemRequest) {
        return itemRequestService.addItemRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero(message = Constant.FROM_MUST_BE_POSITIVE_OR_ZERO) int from,
            @RequestParam(value = "size", defaultValue = "20") @Positive(message = Constant.SIZE_MUST_BE_POSITIVE) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "requestId") long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
