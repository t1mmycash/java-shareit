package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemPostDto;
import ru.practicum.shareit.item.service.ItemRemoteCommandImpl;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @MockBean
    private ItemRemoteCommandImpl command;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addItem_whenItemNameIsBlank_thenResponseStatusBadRequest() {
        ItemPostDto itemWithBlankName = ItemPostDto.builder()
                .id(1L)
                .name(" ")
                .description("description")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemWithBlankName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID_HEADER, "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void addItem_whenItemDescriptionIsBlank_thenResponseStatusBadRequest() {
        ItemPostDto itemWithBlankDescription = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description(" ")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemWithBlankDescription))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID_HEADER, "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void addItem_whenItemAvailableIsNull_thenResponseStatusBadRequest() {
        ItemPostDto itemWithAvailableIsNull = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(null)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemWithAvailableIsNull))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID_HEADER, "1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void addComment_whenCommentTextIsBlank_thenResponseStatusBadRequest() {
        CommentPostDto comment = CommentPostDto.builder()
                .text(" ")
                .build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header(Constant.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?from=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?size=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?size=0")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(command);
    }
}