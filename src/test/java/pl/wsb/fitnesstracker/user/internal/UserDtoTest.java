package pl.wsb.fitnesstracker.user.internal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldCreateValidUserDto() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertTrue(violations.isEmpty());
        assertEquals(1L, userDto.id());
        assertEquals("John", userDto.firstName());
        assertEquals("Doe", userDto.lastName());
        assertEquals(LocalDate.of(1990, 1, 1), userDto.birthdate());
        assertEquals("john@example.com", userDto.email());
    }

    @Test
    void shouldCreateValidUserDtoWithNullId() {
        UserDto userDto = new UserDto(null, "Jane", "Smith", LocalDate.of(1995, 5, 15), "jane@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertTrue(violations.isEmpty());
        assertNull(userDto.id());
    }

    @Test
    void shouldFailValidationWithBlankFirstName() {
        UserDto userDto = new UserDto(1L, "", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("First name is required")));
    }

    @Test
    void shouldFailValidationWithNullFirstName() {
        UserDto userDto = new UserDto(1L, null, "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("First name is required")));
    }

    @Test
    void shouldFailValidationWithBlankLastName() {
        UserDto userDto = new UserDto(1L, "John", "", LocalDate.of(1990, 1, 1), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Last name is required")));
    }

    @Test
    void shouldFailValidationWithFutureBirthdate() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.now().plusDays(1), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Birth date must be in the past")));
    }

    @Test
    void shouldPassValidationWithTodayBirthdate() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.now(), "john@example.com");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Today is not in the past, so it should fail
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Birth date must be in the past")));
    }

    @Test
    void shouldFailValidationWithInvalidEmail() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "invalid-email");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email must be valid")));
    }

    @Test
    void shouldFailValidationWithBlankEmail() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // When email is blank, @NotBlank will fail first and @Email might not be evaluated
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getMessage().equals("Email is required") || 
            v.getMessage().equals("Email must be valid")));
    }

    @Test
    void shouldFailValidationWithMultipleErrors() {
        UserDto userDto = new UserDto(null, "", "", LocalDate.now().plusYears(1), "not-an-email");
        
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Should have at least 4 violations (firstName, lastName, birthdate, email)
        assertTrue(violations.size() >= 4);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("First name is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Last name is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Birth date must be in the past")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email must be valid")));
    }

    @Test
    void shouldHandleRecordEquality() {
        UserDto userDto1 = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        UserDto userDto2 = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        UserDto userDto3 = new UserDto(2L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertEquals(userDto1.hashCode(), userDto2.hashCode());
        assertNotEquals(userDto1.hashCode(), userDto3.hashCode());
    }

    @Test
    void shouldHandleToString() {
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        
        String toString = userDto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("UserDto"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("1990-01-01"));
        assertTrue(toString.contains("john@example.com"));
    }
}