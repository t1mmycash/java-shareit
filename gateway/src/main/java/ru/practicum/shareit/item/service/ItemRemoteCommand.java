package ru.practicum.shareit.item.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

public interface ItemRemoteCommand {
    ResponseEntity<Object> addItem(Long ownerId, ItemPostDto itemDto);

    ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateDto itemDto);

    ResponseEntity<Object> getItemById(Long userId, Long itemId);

    ResponseEntity<Object> getItemsByOwnerId(Long ownerId, Integer from, Integer size);

    ResponseEntity<Object> searchItemsByNameOrDescription(String text, Integer from, Integer size, Long ownerId);

    ResponseEntity<Object> addComment(Long userId, CommentPostDto comment, Long itemId);

}
