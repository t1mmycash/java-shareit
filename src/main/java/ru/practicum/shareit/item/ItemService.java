package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    public ItemDto addItem(ItemDto itemDto, Long userId);

    public List<ItemDto> getAllUserItems(Long userId);

    public ItemDto getItemById(long itemId);

    public ItemDto updateItem(Long userId, long itemId, Map<String, Object> params);

    public List<ItemDto> searchItem(String text);
}
