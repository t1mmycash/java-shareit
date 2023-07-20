package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select new ru.practicum.shareit.item.dto.ItemDto(it.id, it.name, it.description, it.available) " +
            "from Item as it where it.owner.id = ?1")
    List<ItemDto> findItemsByOwnerId(Long ownerId, PageRequest pageRequest);

    <T> Optional<T> findItemById(Long itemId, Class<T> type);

    @Query("select new ru.practicum.shareit.item.dto.ItemDto (it.id, it.name, it.description, it.available) " +
            "from Item as it " +
            "where (lower(it.name) like lower(concat('%',?1,'%')) or " +
            "lower(it.description) like lower(concat('%',?1,'%'))) and it.available = true")
    List<ItemDto> searchItemsByText(String text, PageRequest pageRequest);

    @Query("select new ru.practicum.shareit.item.dto.ItemInItemRequestDto(" +
            "it.id, it.name, it.description, it.owner.id, it.available, it.request.id) " +
            "from Item as it where it.request.id = ?1")
    List<ItemInItemRequestDto> getAllResponsesByRequestId(long requestId);
}
