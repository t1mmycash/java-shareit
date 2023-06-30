package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<ItemDto> findItemsByOwnerId(Long ownerId);

    <T> Optional<T> findItemById(Long itemId, Class<T> type);

    @Query("select new ru.practicum.shareit.item.dto.ItemDto (it.id, it.name, it.description, it.available) " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%',?1,'%')) or " +
            "lower(it.description) like lower(concat('%',?1,'%'))) and it.available = true")
    List<ItemDto> searchItemsByText(String text);


}
