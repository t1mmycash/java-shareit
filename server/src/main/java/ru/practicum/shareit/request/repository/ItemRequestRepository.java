package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestWithoutResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequestWithoutResponsesDto> getAllByRequester_IdOrderByCreatedDesc(long userId);

    List<ItemRequestWithoutResponsesDto> findAllByRequester_IdNot(long userId, PageRequest ageRequest);

    Optional<ItemRequestWithoutResponsesDto> getItemRequestWithoutResponsesDtoById(long requestId);
}
