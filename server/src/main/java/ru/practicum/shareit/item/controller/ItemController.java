package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemPostDto addItem(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @RequestBody ItemPostDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader(value = Constant.USER_ID_HEADER) long userId,
            @PathVariable(value = "itemId") long itemId,
            @RequestBody Comment comment) {
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
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "20") int size) {
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
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return itemService.searchItem(text, from, size);
    }

}
