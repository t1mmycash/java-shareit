package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addItemRequest(long userId, ItemRequest itemRequest) {
        User requester = getUserById(userId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(requester);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(long userId) {
        userExistenceCheck(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequestWithoutResponsesDto request : itemRequestRepository
                .getAllByRequester_IdOrderByCreatedDesc(userId)) {
            result.add(itemRequestMapper.toItemRequestDto(
                    request, itemRepository.getAllResponsesByRequestId(request.getId())));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        userExistenceCheck(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        PageRequest pageRequest;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        if (from == 0) {
            pageRequest = PageRequest.of(from, size, sort);
        } else {
            pageRequest = PageRequest.of(from / size, size, sort);
        }
        for (ItemRequestWithoutResponsesDto request : itemRequestRepository.findAllByRequester_IdNot(userId, pageRequest)) {
            result.add(itemRequestMapper.toItemRequestDto(
                    request, itemRepository.getAllResponsesByRequestId(request.getId())));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userExistenceCheck(userId);
        ItemRequestWithoutResponsesDto request = itemRequestRepository.getItemRequestWithoutResponsesDtoById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(
                        String.format("Запрос с id = %d не существует", requestId)));
        return itemRequestMapper.toItemRequestDto(
                request, itemRepository.getAllResponsesByRequestId(requestId));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("Пользователя с id = %d не существует", userId)));
    }

    private void userExistenceCheck(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователя с id = %d не существует", userId));
        }
    }


}
