package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ItemStorage {
    private long newId = 1;
    private final HashMap<Long, Item> items = new HashMap<>();

    public ItemDto addItem(ItemDto itemDto, long userId) {
        Item item = new Item(
                newId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userId,
                null
        );
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getAllUserItems(long userId) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId() == userId) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(items.get(itemId));
    }

    public ItemDto updateItem(long itemId, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            switch (entry.getKey()) {
                case ("name"):
                    items.get(itemId).setName(entry.getValue().toString());
                    break;
                case ("description"):
                    items.get(itemId).setDescription(entry.getValue().toString());
                    break;
                case ("available"):
                    items.get(itemId).setAvailable(Boolean.parseBoolean(entry.getValue().toString()));
                    break;
            }
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    public List<ItemDto> searchItem(String text) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) && item.isAvailable()) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    public void itemExistenceCheck(long itemId) {
        if (!items.containsKey(itemId)) {
            String message = String.format("Вещи с id = %d не существует", itemId);
            log.warn(message);
            throw new ItemNotFoundException(message);
        }
    }

    public void accessCheck(long userId, long itemId) {
        if (items.get(itemId).getOwnerId() != userId) {
            String message = String.format("У пользователя с id - %d нет доступа к вещи с id - %d", userId, itemId);
            log.warn(message);
            throw new AccessDeniedException(message);
        }
    }

    private long newId() {
        return newId++;
    }
}
