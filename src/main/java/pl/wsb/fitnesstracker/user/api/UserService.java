package pl.wsb.fitnesstracker.user.api;

import java.time.LocalDate;

/**
 * Interface (API) for modifying operations on {@link User} entities through the API.
 * Implementing classes are responsible for executing changes within a database transaction, whether by continuing an existing transaction or creating a new one if required.
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param user the user to create
     * @return the created user with generated ID
     * @throws IllegalArgumentException if user already has an ID
     */
    User createUser(User user);

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUserById(Long id);

    /**
     * Updates an existing user with new data.
     *
     * @param userId the ID of the user to update
     * @param firstName the new first name (optional, null to keep current)
     * @param lastName the new last name (optional, null to keep current)
     * @param email the new email (optional, null to keep current)
     * @param birthdate the new birthdate (optional, null to keep current)
     * @throws jakarta.persistence.EntityNotFoundException if user not found
     */
    void updateUser(Long userId, String firstName, String lastName, String email, LocalDate birthdate);
}
