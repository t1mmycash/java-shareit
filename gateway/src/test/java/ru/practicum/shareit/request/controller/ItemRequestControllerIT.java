package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestPostDto;
import ru.practicum.shareit.request.service.RequestRemoteCommand;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerIT {
    @MockBean
    private RequestRemoteCommand command;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addItemRequest_whenDescriptionIsNull_thenResponseStatusBadRequest() {
        RequestPostDto itemRequest = RequestPostDto.builder().build();

        mvc.perform(post("/requests")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void addItemRequest_whenDescriptionIsBlank_thenResponseStatusBadRequest() {
        RequestPostDto itemRequest = RequestPostDto.builder()
                .description(" ")
                .build();

        mvc.perform(post("/requests")
                        .header(Constant.USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests/all?from=-1", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests/all?size=-1", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/requests/all?size=0", 1L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }
}