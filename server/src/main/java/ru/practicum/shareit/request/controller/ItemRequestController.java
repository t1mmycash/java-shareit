package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestHeader(value = USER_ID_HEADER) long userId,
            @RequestBody ItemRequest itemRequest) {
        return itemRequestService.addItemRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(value = USER_ID_HEADER) long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(value = USER_ID_HEADER) long userId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(value = USER_ID_HEADER) long userId,
            @PathVariable(value = "requestId") long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
