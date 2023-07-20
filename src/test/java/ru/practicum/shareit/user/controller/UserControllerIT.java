package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void addUser_whenInvoked_thenResponseStatusOkWithUserInBody() {
        User user = User.builder()
                .name("name")
                .email("email@gmail.com")
                .build();
        when(userService.addUser(user)).thenReturn(user);

        String result = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(user), result);
        verify(userService, times(1)).addUser(user);
    }

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
    void updateUser_whenInvoked_thenResponseStatusOkAndUserInBody() {
        UserUpdateDto updates = new UserUpdateDto("name", "email@gmail.com");
        User user = new User(1L, "name", "email@gmail.com");
        when(userService.updateUser(1L, updates)).thenReturn(user);

        String result = mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(updates))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(user), result);
        verify(userService, times(1)).updateUser(1L, updates);
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

        verify(userService, never()).updateUser(1, updates);
    }

    @Test
    @SneakyThrows
    void getAllUsers_whenInvoked_thenResponseStatusOkAndUsersListInBody() {
        List<User> users = List.of(
                new User(1L, "name1", "email1@gmail.com"),
                new User(2L, "name2", "email2@gmail.com"));
        when(userService.getAllUsers()).thenReturn(users);

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(users), result);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void getUserById_whenInvoked_thenResponseStatusOkAndUserInBody() {
        User user = new User(1L, "name", "email@gmail.com");
        when(userService.getUserById(1L)).thenReturn(user);

        String result = mvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(user), result);
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvoked_thenResponseStatusOk() {
        doNothing().when(userService).deleteUser(1L);

        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }

}
