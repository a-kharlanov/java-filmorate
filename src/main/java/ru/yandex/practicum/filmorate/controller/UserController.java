package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    private long nextId = 1;

    private long getNextId() {
        return nextId++;
    }

    private void fillNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя взято из логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        fillNameIfEmpty(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан, id = {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser);
        if (!users.containsKey(newUser.getId())) {
            log.warn("Не удалось обновить: пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        fillNameIfEmpty(newUser);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлён, id = {}", newUser.getId());
        return newUser;
    }
}
