package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerIT {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void addItem_whenInvoked_thenResponseStatusOkAndItemPostDtoInBody() {
        ItemPostDto item = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemService.addItem(item, 1L)).thenReturn(item);

        String result = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(item), result);
        verify(itemService, times(1)).addItem(item, 1L);
    }

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

        verify(itemService, never()).addItem(any(ItemPostDto.class), anyLong());
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

        verify(itemService, never()).addItem(any(ItemPostDto.class), anyLong());
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

        verify(itemService, never()).addItem(any(ItemPostDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void addItem_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        ItemPostDto item = ItemPostDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(any(ItemPostDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void addComment_whenInvoked_thenResponseStatusOkAndCommentResponseDtoInBody() {
        Comment comment = Comment.builder()
                .text("text")
                .build();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(1L, 2L, comment)).thenReturn(commentResponseDto);

        String result = mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header(Constant.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(commentResponseDto), result);
        verify(itemService, times(1)).addComment(1L, 2L, comment);
    }

    @Test
    @SneakyThrows
    void addComment_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        Comment comment = Comment.builder()
                .text("text")
                .build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(anyLong(), anyLong(), any(Comment.class));
    }

    @Test
    @SneakyThrows
    void addComment_whenCommentTextIsBlank_thenResponseStatusBadRequest() {
        Comment comment = Comment.builder()
                .text(" ")
                .build();

        mvc.perform(post("/items/{itemId}/comment", 2L)
                        .header(Constant.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(anyLong(), anyLong(), any(Comment.class));
    }

    @Test
    @SneakyThrows
    void updateItem_whenNameDescriptionAvailableWillBeUpdated_thenResponseStatusOkAndItemDtoInBody() {
        ItemUpdateDto updates = ItemUpdateDto.builder()
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("updatedName")
                .description("updatedDescription")
                .available(true)
                .build();
        when(itemService.updateItem(1L, 2L, updates)).thenReturn(itemDto);

        String result = mvc.perform(patch("/items/{itemId}", 2L)
                        .header(Constant.USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
        verify(itemService, times(1)).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        ItemUpdateDto updates = new ItemUpdateDto();

        mvc.perform(patch("/items/{itemId}", 2L)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class));
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenInvokedWithoutFromAndSize_thenResponseStatusOkAndItemGetResponseDtoListInBody() {
        List<ItemGetResponseDto> items = Collections.emptyList();
        when(itemService.getAllUserItems(1L, 0, 20)).thenReturn(items);

        String result = mvc.perform(get("/items")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(items), result);
        verify(itemService, times(1)).getAllUserItems(1L, 0, 20);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenInvokedWithFromAndSize_thenResponseStatusOkAndItemGetResponseDtoListInBody() {
        List<ItemGetResponseDto> items = Collections.emptyList();
        when(itemService.getAllUserItems(1L, 10, 30)).thenReturn(items);

        String result = mvc.perform(get("/items?from=10&size=30")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(items), result);
        verify(itemService, times(1)).getAllUserItems(1L, 10, 30);
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?from=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?size=-1")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/items?size=0")
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllUserItems_whenNoUserIdHeader_thenResponseStatusBadRequest() {
        mvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemById_whenInvoked_thenResponseStatusOkAndItemGetResponseDtoInBody() {
        ItemGetResponseDto itemGetResponseDto = ItemGetResponseDto.builder()
                .id(2L)
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemService.getItemResponseDtoById(1L, 2L)).thenReturn(itemGetResponseDto);

        String result = mvc.perform(get("/items/{itemId}", 2L)
                        .header(Constant.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemGetResponseDto), result);
        verify(itemService, times(1)).getItemResponseDtoById(1L, 2L);
    }

    @Test
    @SneakyThrows
    void getItemById_whenNoUserIdHeader_whenResponseStatusBadRequest() {
        mvc.perform(get("/items/{itemId}", 2L))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemResponseDtoById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void searchItems_whenInvokedWithoutFromAndSize_thenResponseStatusOkAndItemDtoListInBody() {
        List<ItemDto> items = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("name1")
                        .description("description1")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build());
        when(itemService.searchItem("text", 0, 20)).thenReturn(items);

        String result = mvc.perform(get("/items/search?text=text"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(items), result);
        verify(itemService, times(1)).searchItem("text", 0, 20);
    }

    @Test
    @SneakyThrows
    void searchItems_whenInvokedWithFromAndSize_thenResponseStatusOkAndItemDtoListInBody() {
        List<ItemDto> items = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("name1")
                        .description("description1")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("name2")
                        .description("description2")
                        .available(true)
                        .build());
        when(itemService.searchItem("text", 10, 30)).thenReturn(items);

        String result = mvc.perform(get("/items/search?text=text&from=10&size=30"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(items), result);
        verify(itemService, times(1)).searchItem("text", 10, 30);
    }

    @Test
    @SneakyThrows
    void searchItems_whenInvokedWithoutText_thenResponseStatusBadRequest() {
        mvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItems_whenFromIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items/search?from=-1"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItems_whenSizeIsNegative_thenResponseStatusBadRequest() {
        mvc.perform(get("/items/search?size=-1"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItem(anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItems_whenSizeIsZero_thenResponseStatusBadRequest() {
        mvc.perform(get("/items/search?size=0"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).searchItem(anyString(), anyInt(), anyInt());
    }
}