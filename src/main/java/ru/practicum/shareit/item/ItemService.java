package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    public ItemDto addItem(ItemDto itemDto, Long userId);

    public List<ItemDto> getAllUserItems(Long userId);

    public ItemDto getItemById(long itemId);

    public ItemDto updateItem(Long userId, long itemId, ItemUpdateDto changes);

    public List<ItemDto> searchItem(String text);
}
