package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.LackOfInformationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (itemDto.getDescription() == null || itemDto.getName() == null || itemDto.getAvailable() == null) {
            throw new LackOfInformationException("Заполнены не все поля");
        }
        if (itemDto.getDescription().isBlank() || itemDto.getName().isBlank()) {
            throw new LackOfInformationException("Заполнены не все поля");
        }
        userIdNotNullCheck(userId);
        userStorage.userExistenceCheck(userId);
        return itemStorage.addItem(itemDto, userId);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        userIdNotNullCheck(userId);
        userStorage.userExistenceCheck(userId);
        return itemStorage.getAllUserItems(userId);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        itemStorage.itemExistenceCheck(itemId);
        return itemStorage.getItemById(itemId);
    }

    @Override
    public ItemDto updateItem(Long userId, long itemId, Map<String, Object> params) {
        userIdNotNullCheck(userId);
        userStorage.userExistenceCheck(userId);
        itemStorage.itemExistenceCheck(itemId);
        itemStorage.accessCheck(userId, itemId);
        return itemStorage.updateItem(itemId, params);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchItem(text.toLowerCase());
    }

    private void userIdNotNullCheck(Long userId) {
        if (userId == null) {
            String message = "Пустой идентификатор пользователя";
            log.warn(message);
            throw new LackOfInformationException(message);
        }
    }

}
