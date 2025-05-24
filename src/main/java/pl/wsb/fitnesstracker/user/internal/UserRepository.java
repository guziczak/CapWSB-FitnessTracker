package pl.wsb.fitnesstracker.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findFirst();
    }

    /**
     * Query searching users by email fragment (case-insensitive).
     * This implementation uses findAll() and streams as per requirements.
     *
     * @param email email fragment to search
     * @return list of users whose email contains the fragment
     */
    default List<User> findByPartOfEmail(String email) {
        return findAll().stream()
                .filter(el -> el.getEmail().toLowerCase().contains(email.toLowerCase()))
                .toList();
    }

    /**
     * Query searching users born before the specified date.
     *
     * @param date the cutoff date
     * @return list of users born before the date
     */
    @Query("SELECT u FROM User u WHERE u.birthdate < :date")
    List<User> findByBirthdateBefore(@Param("date") LocalDate date);
}
