package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addItemRequest_whenInvoked_thenResponseStatusOkAndItemRequestDtoInBody() {
        ItemRequest itemRequest = ItemRequest.builder()
                .description("description")
                .build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .build();
        when(itemRequestService.addItemRequest(1L, itemRequest)).thenReturn(itemRequestDto);

        String result = mvc.perform(post("/requests")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDto), result);
        verify(itemRequestService, times(1)).addItemRequest(1L, itemRequest);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void addItemRequest_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(post("/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getUserRequests_whenInvoked_thenResponseStatusOkAndItemRequestDtoListInBody() {
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder()
                        .id(1L)
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .build());
        when(itemRequestService.getUserRequests(1L)).thenReturn(requests);

        String result = mvc.perform(get("/requests")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(requests), result);
        verify(itemRequestService, times(1)).getUserRequests(1L);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getUserRequests_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenInvoked_thenResponseStatusOkAndItemRequestDtoInBody() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .build();
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(itemRequestDto);

        String result = mvc.perform(get("/requests/{requestId}", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDto), result);
        verify(itemRequestService, times(1)).getRequestById(1L, 1L);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getRequestById_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenInvokedWithFromAndSize_thenResponseStatusOkAndItemRequestDtoListInBody() {
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder()
                        .id(1L)
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .build());
        when(itemRequestService.getAllRequests(1L, 10, 30)).thenReturn(requests);

        String result = mvc.perform(get("/requests/all?from=10&size=30")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(requests), result);
        verify(itemRequestService, times(1)).getAllRequests(1L, 10, 30);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenInvokedWithoutFromAndSize_thenResponseStatusOkAndItemRequestDtoListInBody() {
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder()
                        .id(1L)
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .build());
        when(itemRequestService.getAllRequests(1L, 0, 20)).thenReturn(requests);

        String result = mvc.perform(get("/requests/all")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(requests), result);
        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 20);
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests/all", 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestService);
    }
}