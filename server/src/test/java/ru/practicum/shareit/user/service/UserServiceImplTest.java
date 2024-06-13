package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    @Autowired
    private final UserMapper mapper;
    @Autowired
    private final UserService service;
    private final EntityManager em;

    @Test
    void add() {
        UserDto userDto = new UserDto(null, "Ivan", "iv@mail.ru");

        service.add(userDto);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void get() {
        UserDto userDto = new UserDto(null, "Ivan", "iv@mail.ru");
        UserDto userDto1 = new UserDto(null, "Ptr", "ptr@mail.ru");
        UserDto userDto2 = new UserDto(null, "Dns", "dns@mail.ru");

        List<UserDto> users = new ArrayList<>();
        users.add(userDto);
        users.add(userDto1);
        users.add(userDto2);

        for (UserDto user : users) {
            User entity = mapper.toEntity(user);
            em.persist(entity);
        }
        em.flush();

        // when
        List<UserDto> targetUsers = service.get();

        // then
        assertThat(targetUsers, hasSize(users.size()));
        for (UserDto sourceUser : users) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void getById() {
        UserDto userDto = new UserDto(null, "Tolia", "lia@mail.ru");

        em.persist(mapper.toEntity(userDto));
        em.flush();

        TypedQuery<User> query = em.createQuery("select u from User as u where u.name = :name", User.class);
        UserDto user = service.getById(query.setParameter("name", userDto.getName()).getSingleResult().getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto(null, "Tolia", "lia@mail.ru");

        em.persist(mapper.toEntity(userDto));
        em.flush();

        UserDto update = new UserDto(null, null, "izh@ya.ru");
        TypedQuery<User> query = em.createQuery("select u from User as u where u.name = :name", User.class);
        UserDto user = service.update(query.setParameter("name", userDto.getName()).getSingleResult().getId(), update);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(update.getEmail()));
    }

    @Test
    void delete() {
        UserDto userDto = new UserDto(null, "del", "delete@mail.ru");

        em.persist(mapper.toEntity(userDto));
        em.flush();

        TypedQuery<User> query = em.createQuery("select u from User as u where u.name = :name", User.class);

        service.delete(query.setParameter("name", userDto.getName()).getSingleResult().getId());

        List<UserDto> targetUsers = service.get();
        assertThat(targetUsers, hasSize(0));
    }

    @Test
    void notFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getById(100L));

        assertEquals("User: Пользователь с id=100 не найден", exception.getMessage());

    }
}