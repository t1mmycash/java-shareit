package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserRemoteCommandImpl;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserRemoteCommandImpl userService;

    @Test
    @SneakyThrows
    void addUser_whenUserNameNotValid_thenResponseStatusBadRequest() {
        User userWithInvalidName = User.builder()
                .name(" ")
                .email("email@gmail.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userWithInvalidName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(userWithInvalidName);
    }

    @Test
    @SneakyThrows
    void addUser_whenUserEmailIsBlank_thenResponseStatusBadRequest() {
        User userWithInvalidEmail = User.builder()
                .name("name")
                .email(" ")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userWithInvalidEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any());
    }

    @Test
    @SneakyThrows
    void addUser_whenUserEmailNotValid_thenResponseStatusBadRequest() {
        User userWithInvalidEmail = User.builder()
                .name("name")
                .email("email")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userWithInvalidEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any());
    }

    @Test
    @SneakyThrows
    void updateUser_whenEmailNotValid_thenResponseStatusBadRequest() {
        UserUpdateDto updates = UserUpdateDto.builder()
                .email("email")
                .build();

        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(1L, updates);
    }
}