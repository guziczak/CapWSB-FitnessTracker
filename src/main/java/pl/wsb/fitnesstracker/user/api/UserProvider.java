package pl.wsb.fitnesstracker.user.api;

import java.util.List;
import java.util.Optional;

public interface UserProvider {

    /**
     * Retrieves a user based on their ID.
     * If the user with given ID is not found, then {@link Optional#empty()} will be returned.
     *
     * @param userId id of the user to be searched
     * @return An {@link Optional} containing the located user, or {@link Optional#empty()} if not found
     */
    Optional<User> getUser(Long userId);

    /**
     * Retrieves a user based on their email.
     * If the user with given email is not found, then {@link Optional#empty()} will be returned.
     *
     * @param email The email of the user to be searched
     * @return An {@link Optional} containing the located user, or {@link Optional#empty()} if not found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Retrieves users whose email contains the given fragment (case-insensitive).
     *
     * @param email the email fragment to search for
     * @return A list of users whose email contains the given fragment
     */
    List<User> getUsersByPartOfEmail(String email);

    /**
     * Retrieves all users.
     *
     * @return A list containing all users
     */
    List<User> findAllUsers();

    /**
     * Retrieves users older than the specified age.
     *
     * @param age the minimum age
     * @return A list of users older than the specified age
     */
    List<User> getUsersOlderThan(int age);

}
