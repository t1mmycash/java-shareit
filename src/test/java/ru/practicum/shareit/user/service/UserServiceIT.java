package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIT {
    private final EntityManager em;
    private final UserService service;

    private long userId;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("User")
                .email("user@mail.ru")
                .build();
        userId = service.addUser(user).getId();
    }

    @Test
    void getAll() {
        User user1 = User.builder()
                .name("User1")
                .email("user1@mail.ru").build();
        User user2 = User.builder()
                .name("User2")
                .email("user2@mail.ru").build();
        User user3 = User.builder()
                .name("User3")
                .email("user3@mail.ru").build();

        List<User> sourceUsers = new ArrayList<>(List.of(user1, user2, user3));

        for (User sourceUser : sourceUsers) {
            service.addUser(sourceUser);
        }
        user.setId(userId);
        sourceUsers.add(user);

        List<User> targetUsers = service.getAllUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void findById() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user1 = query.setParameter("id", userId).getSingleResult();

        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void create() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        assertEquals(user1.getName(), user.getName());
        assertEquals(user1.getEmail(), user.getEmail());
    }

    @Test
    void delete() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        service.deleteUser(user1.getId());
        TypedQuery<User> queryAfterDelete = em.createQuery(
                "Select u from User u where u.email = :email", User.class);
        List<User> userResult = queryAfterDelete.setParameter("email", user.getEmail()).getResultList();

        assertTrue(userResult.isEmpty());
    }

    @Test
    void update() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", user.getEmail()).getSingleResult();

        UserUpdateDto updateDto = UserUpdateDto.builder()
                .name("Update")
                .email("user@mail.ru").build();
        service.updateUser(user1.getId(), updateDto);

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userUpdated = query.setParameter("email", updateDto.getEmail().get()).getSingleResult();

        assertTrue(updateDto.getName().isPresent());
        assertTrue(updateDto.getEmail().isPresent());
        assertEquals(userUpdated.getName(), updateDto.getName().get());
        assertEquals(userUpdated.getEmail(), updateDto.getEmail().get());
    }


}