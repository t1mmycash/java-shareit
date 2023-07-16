package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemPostDto addItem(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestBody @Valid ItemPostDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "itemId") long itemId,
            @RequestBody @Valid Comment comment) {
        return itemService.addComment(userId, itemId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "itemId") long itemId,
            @RequestBody ItemUpdateDto changes) {
        return itemService.updateItem(userId, itemId, changes);
    }

    @GetMapping
    public List<ItemGetResponseDto> getAllUserItems(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero(message = Constant.FROM_MUST_BE_POSITIVE_OR_ZERO) int from,
            @RequestParam(value = "size", defaultValue = "20") @Positive(message = Constant.SIZE_MUST_BE_POSITIVE) int size) {
        return itemService.getAllUserItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemGetResponseDto getItemById(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "itemId") long itemId) {
        return itemService.getItemResponseDtoById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero(message = Constant.FROM_MUST_BE_POSITIVE_OR_ZERO) int from,
            @RequestParam(value = "size", defaultValue = "20") @Positive(message = Constant.SIZE_MUST_BE_POSITIVE) int size) {
        return itemService.searchItem(text, from, size);
    }

}
