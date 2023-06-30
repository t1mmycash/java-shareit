package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemGetResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    public ItemDto addItem(ItemDto itemDto, long userId);

    public CommentResponseDto addComment(long userId, long itemId, Comment comment);

    public List<ItemGetResponseDto> getAllUserItems(long userId);

    public ItemGetResponseDto getItemResponseDtoById(long userId, long itemId);

    public ItemDto updateItem(long userId, long itemId, ItemUpdateDto changes);

    public List<ItemDto> searchItem(String text);
}
