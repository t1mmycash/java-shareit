package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemInItemRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Test
    void addItemRequest_whenInvoked_thenReturnItemRequestDto() {
        User requester = User.builder()
                .id(1L)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList())).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.addItemRequest(1L, itemRequest);

        assertEquals(itemRequestDto, result);
        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRequestMapper);
        inOrder.verify(userRepository, times(1)).findById(1L);
        inOrder.verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        inOrder.verify(itemRequestMapper, times(1))
                .toItemRequestDto(itemRequest, Collections.emptyList());
        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRequestMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void addItemRequest_whenUserNotFound_thenExceptionWillBeThrown() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemRequestService.addItemRequest(1L, itemRequest));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, itemRequestRepository, itemRequestMapper);
    }

    @Test
    void getRequestById_whenInvoked_thenReturnItemRequestDto() {
        ItemRequestWithoutResponsesDto request = ItemRequestWithoutResponsesDto.builder()
                .id(1L)
                .build();
        ItemInItemRequestDto item = ItemInItemRequestDto.builder()
                .id(1L)
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .items(List.of(item))
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.getItemRequestWithoutResponsesDtoById(1L))
                .thenReturn(Optional.of(request));
        when(itemRepository.getAllResponsesByRequestId(1L)).thenReturn(List.of(item));
        when(itemRequestMapper.toItemRequestDto(request, List.of(item))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getRequestById(1L, 1L);

        assertEquals(itemRequestDto, result);
        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRequestRepository, times(1)).getItemRequestWithoutResponsesDtoById(1L);
        inOrder.verify(itemRepository, times(1)).getAllResponsesByRequestId(1L);
        inOrder.verify(itemRequestMapper, times(1)).toItemRequestDto(request, List.of(item));
        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
    }

    @Test
    void getRequestById_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRequestRepository, itemRepository, itemRequestMapper);
    }

    @Test
    void getRequestById_whenRequestNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.getItemRequestWithoutResponsesDtoById(1L))
                .thenReturn(Optional.empty());

        ItemRequestNotFoundException e = assertThrows(
                ItemRequestNotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));

        assertEquals("Запрос с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verify(itemRequestRepository, times(1)).getItemRequestWithoutResponsesDtoById(1L);
        verifyNoMoreInteractions(userRepository, itemRequestRepository);
        verifyNoInteractions(itemRepository, itemRequestMapper);
    }

    @Test
    void getUserRequests_whenInvoked_thenReturnItemRequestDtoList() {
        ItemRequestWithoutResponsesDto itemRequest = ItemRequestWithoutResponsesDto.builder()
                .id(1L)
                .build();
        ItemInItemRequestDto item = ItemInItemRequestDto.builder()
                .id(1L)
                .build();
        List<ItemRequestWithoutResponsesDto> requests = List.of(itemRequest);
        List<ItemInItemRequestDto> items = List.of(item);
        ItemRequestDto resultRequest = ItemRequestDto.builder()
                .id(1L)
                .build();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(1L)).thenReturn(requests);
        when(itemRepository.getAllResponsesByRequestId(1L)).thenReturn(items);
        when(itemRequestMapper.toItemRequestDto(itemRequest, items)).thenReturn(resultRequest);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(1L);

        assertEquals(List.of(resultRequest), result);
        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRequestRepository, times(1))
                .getAllByRequester_IdOrderByCreatedDesc(1L);
        inOrder.verify(itemRepository, times(1)).getAllResponsesByRequestId(1L);
        inOrder.verify(itemRequestMapper, times(1)).toItemRequestDto(itemRequest, items);
        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
    }

    @Test
    void getUserRequests_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemRequestService.getUserRequests(1L));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, itemRequestRepository, itemRequestMapper);
    }

    @Test
    void getAllRequests_whenInvoked_thenReturnItemRequestDtoList() {
        ItemRequestWithoutResponsesDto requestWithoutResponsesDto = ItemRequestWithoutResponsesDto.builder()
                .id(1L)
                .build();
        List<ItemInItemRequestDto> items = List.of(ItemInItemRequestDto.builder()
                .id(1L)
                .build());
        ItemRequestDto resultRequest = ItemRequestDto.builder()
                .id(1L)
                .build();
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRequestRepository.findAllByRequester_IdNot(1L, PageRequest.of(0, 20, sort)))
                .thenReturn(List.of(requestWithoutResponsesDto));
        when(itemRepository.getAllResponsesByRequestId(1L)).thenReturn(items);
        when(itemRequestMapper.toItemRequestDto(requestWithoutResponsesDto, items))
                .thenReturn(resultRequest);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, 0, 20);

        assertEquals(List.of(resultRequest), result);
        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
        inOrder.verify(userRepository, times(1)).existsById(1L);
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequester_IdNot(1L, PageRequest.of(0, 20, sort));
        inOrder.verify(itemRepository, times(1)).getAllResponsesByRequestId(1L);
        inOrder.verify(itemRequestMapper, times(1))
                .toItemRequestDto(requestWithoutResponsesDto, items);
        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository, itemRequestMapper);
    }

    @Test
    void getAllRequests_whenUserNotFound_thenExceptionWillBeThrown() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException e = assertThrows(
                UserNotFoundException.class, () -> itemRequestService.getAllRequests(1L, 0, 20));

        assertEquals("Пользователя с id = 1 не существует", e.getMessage());
        verify(userRepository, times(1)).existsById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemRepository, itemRequestRepository, itemRequestMapper);
    }
}