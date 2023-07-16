package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemPostDto addItem(ItemPostDto itemDto, long userId);

    CommentResponseDto addComment(long userId, long itemId, Comment comment);

    List<ItemGetResponseDto> getAllUserItems(long userId, int from, int size);

    ItemGetResponseDto getItemResponseDtoById(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, ItemUpdateDto changes);

    List<ItemDto> searchItem(String text, int from, int size);
}
