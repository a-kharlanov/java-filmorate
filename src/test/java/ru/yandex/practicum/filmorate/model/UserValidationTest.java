package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {
    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    private User validUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 5, 20));
        return user;
    }

    private boolean hasViolationOn(Set<ConstraintViolation<User>> violations, String field) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    @Test
    void validUserHasNoViolations() {
        assertTrue(validator.validate(validUser()).isEmpty());
    }

    @Test
    void emptyEmailIsInvalid() {
        User user = validUser();
        user.setEmail("");
        assertTrue(hasViolationOn(validator.validate(user), "email"));
    }

    @Test
    void emailWithoutAtIsInvalid() {
        User user = validUser();
        user.setEmail("usermail.ru");
        assertTrue(hasViolationOn(validator.validate(user), "email"));
    }

    @Test
    void emailEndingWithAtIsInvalid() {
        User user = validUser();
        user.setEmail("это-неправильный?эмейл@");
        assertTrue(hasViolationOn(validator.validate(user), "email"));
    }

    @Test
    void emptyLoginIsInvalid() {
        User user = validUser();
        user.setLogin("");
        assertTrue(hasViolationOn(validator.validate(user), "login"));
    }

    @Test
    void loginWithSpaceIsInvalid() {
        User user = validUser();
        user.setLogin("my login");
        assertTrue(hasViolationOn(validator.validate(user), "login"));
    }

    @Test
    void nullNameIsValid() {
        User user = validUser();
        user.setName(null);
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void birthdayTodayIsValid() {
        User user = validUser();
        user.setBirthday(LocalDate.now());
        assertTrue(validator.validate(user).isEmpty());
    }

    @Test
    void birthdayTomorrowIsInvalid() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertTrue(hasViolationOn(validator.validate(user), "birthday"));
    }

    @Test
    void emptyUserHasViolationsOnEmailAndLogin() {
        Set<ConstraintViolation<User>> violations = validator.validate(new User());
        assertTrue(hasViolationOn(violations, "email"));
        assertTrue(hasViolationOn(violations, "login"));
        assertEquals(2, violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .distinct()
                .count());
    }
}