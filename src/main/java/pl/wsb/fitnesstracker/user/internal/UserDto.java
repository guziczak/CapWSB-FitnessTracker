package pl.wsb.fitnesstracker.user.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * Data Transfer Object for User entity.
 * Used for transferring user data between layers.
 *
 * @param id the user ID (nullable for new users)
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param birthdate the user's birth date
 * @param email the user's email address
 */
public record UserDto(
        @Nullable Long id,
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Past(message = "Birth date must be in the past") LocalDate birthdate,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid") String email) {
}
