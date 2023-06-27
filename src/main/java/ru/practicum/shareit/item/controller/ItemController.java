package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final static String USER_ID_NOT_NULL = "id пользователя не может быть null";
    private final static String ITEM_ID_NOT_NULL = "id вещи не может быть null";
    private final static String TEXT_NOT_NULL = "текст не может быть null";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @RequestBody @Valid ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @PathVariable(value = "itemId") @NotNull(message = ITEM_ID_NOT_NULL) Long itemId,
            @RequestBody @Valid Comment comment) {
        return itemService.addComment(userId, itemId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @PathVariable(value = "itemId") @NotNull(message = ITEM_ID_NOT_NULL) Long itemId,
            @RequestBody ItemUpdateDto changes) {
        return itemService.updateItem(userId, itemId, changes);
    }

    @GetMapping
    public List<ItemGetResponseDto> getAllUserItems(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = USER_ID_NOT_NULL) Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemGetResponseDto getItemById(
            @RequestHeader(value = "X-Sharer-User-Id") @NotNull(message = USER_ID_NOT_NULL) Long userId,
            @PathVariable(value = "itemId") @NotNull(message = ITEM_ID_NOT_NULL) Long itemId) {
        return itemService.getItemResponseDtoById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(value = "text") @NotNull(message = TEXT_NOT_NULL) String text) {
        return itemService.searchItem(text);
    }

}
