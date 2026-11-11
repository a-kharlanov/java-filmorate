package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmValidationTest {
    private static final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    private Film validFilm() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    private boolean hasViolationOn(Set<ConstraintViolation<Film>> violations, String field) {
        return violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    @Test
    void validFilmHasNoViolations() {
        assertTrue(validator.validate(validFilm()).isEmpty());
    }

    @Test
    void emptyNameIsInvalid() {
        Film film = validFilm();
        film.setName("");
        assertTrue(hasViolationOn(validator.validate(film), "name"));
    }

    @Test
    void blankNameIsInvalid() {
        Film film = validFilm();
        film.setName("   ");
        assertTrue(hasViolationOn(validator.validate(film), "name"));
    }

    @Test
    void nullNameIsInvalid() {
        Film film = validFilm();
        film.setName(null);
        assertTrue(hasViolationOn(validator.validate(film), "name"));
    }

    @Test
    void descriptionOf200CharsIsValid() {
        Film film = validFilm();
        film.setDescription("a".repeat(200));
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void descriptionOf201CharsIsInvalid() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));
        assertTrue(hasViolationOn(validator.validate(film), "description"));
    }

    @Test
    void releaseDateOnMinBoundaryIsValid() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void releaseDateDayBeforeMinBoundaryIsInvalid() {
        Film film = validFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertTrue(hasViolationOn(validator.validate(film), "releaseDate"));
    }

    @Test
    void nullReleaseDateIsInvalid() {
        Film film = validFilm();
        film.setReleaseDate(null);
        assertTrue(hasViolationOn(validator.validate(film), "releaseDate"));
    }

    @Test
    void durationOfOneIsValid() {
        Film film = validFilm();
        film.setDuration(1);
        assertTrue(validator.validate(film).isEmpty());
    }

    @Test
    void zeroDurationIsInvalid() {
        Film film = validFilm();
        film.setDuration(0);
        assertTrue(hasViolationOn(validator.validate(film), "duration"));
    }

    @Test
    void negativeDurationIsInvalid() {
        Film film = validFilm();
        film.setDuration(-1);
        assertTrue(hasViolationOn(validator.validate(film), "duration"));
    }

    @Test
    void emptyFilmHasViolationsOnAllRequiredFields() {
        Set<ConstraintViolation<Film>> violations = validator.validate(new Film());
        assertEquals(3, violations.size());
    }
}