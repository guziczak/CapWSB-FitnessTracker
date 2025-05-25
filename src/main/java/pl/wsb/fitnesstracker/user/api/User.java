package pl.wsb.fitnesstracker.user.api;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a user in the FitnessTracker system.
 * Each user has personal information and a unique email address.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    @Setter
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank
    @Setter
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank
    @Setter
    private String lastName;

    @Column(name = "birthdate", nullable = false)
    @Past(message = "Birth date must be in the past")
    @Setter
    private LocalDate birthdate;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    @Setter
    private String email;

    /**
     * Creates a new User with the given details.
     *
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param birthdate the user's birth date
     * @param email the user's email address
     */
    public User(
            final String firstName,
            final String lastName,
            final LocalDate birthdate,
            final String email) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.email = email;
    }

}

