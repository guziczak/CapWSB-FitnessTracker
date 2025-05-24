package pl.wsb.fitnesstracker.user.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@SpringJUnitConfig
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        userRepository.deleteAll();
        entityManager.flush();
        
        // Create and persist test users
        // John was born on 1990-01-01 (before 1990-06-01)
        testUser1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        testUser1 = entityManager.persistAndFlush(testUser1);
        
        // Jane was born on 1985-05-15 (before 1990-06-01)
        testUser2 = new User("Jane", "Smith", LocalDate.of(1985, 5, 15), "jane.smith@example.org");
        testUser2 = entityManager.persistAndFlush(testUser2);
        
        // Alice was born on 2000-03-20 (after 1990-06-01)
        testUser3 = new User("Alice", "Brown", LocalDate.of(2000, 3, 20), "alice@test.com");
        testUser3 = entityManager.persistAndFlush(testUser3);
        
        entityManager.clear(); // Clear persistence context to ensure fresh reads
    }

    @Test
    void shouldFindByExactEmail() {
        // Execute
        Optional<User> result = userRepository.findByEmail("john.doe@example.com");

        // Verify
        assertTrue(result.isPresent());
        assertEquals("john.doe@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        // Execute
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Verify
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindByPartOfEmailCaseInsensitive() {
        // Execute
        List<User> result = userRepository.findByPartOfEmail("EXAMPLE");

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("john.doe@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("jane.smith@example.org")));
        assertFalse(result.stream().anyMatch(u -> u.getEmail().equals("alice@test.com")));
    }

    @Test
    void shouldFindByPartOfEmailWithPartialMatch() {
        // Execute
        List<User> result = userRepository.findByPartOfEmail("smith");

        // Verify
        assertEquals(1, result.size());
        assertEquals("jane.smith@example.org", result.get(0).getEmail());
    }

    @Test
    void shouldReturnEmptyListWhenNoEmailMatches() {
        // Execute
        List<User> result = userRepository.findByPartOfEmail("nonexistent");

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindAllUsersWithPartOfEmail() {
        // Execute
        List<User> result = userRepository.findByPartOfEmail("@");

        // Verify
        assertEquals(3, result.size());
    }

    @Test
    void shouldHandleCaseSensitivityInFindByEmail() {
        // Execute - findByEmail is case sensitive
        Optional<User> result = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");

        // Verify
        assertFalse(result.isPresent()); // Should not find because of case sensitivity
    }

    @Test
    void shouldFindByBirthdateBefore() {
        // Setup
        LocalDate cutoffDate = LocalDate.of(1990, 6, 1);

        // Execute
        List<User> result = userRepository.findByBirthdateBefore(cutoffDate);

        // Verify - Both John (1990-01-01) and Jane (1985-05-15) were born before 1990-06-01
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("john.doe@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("jane.smith@example.org")));
    }
    
    @Test
    void shouldReturnEmptyListWhenNoBirthdatesMatch() {
        // Setup
        LocalDate veryOldDate = LocalDate.of(1900, 1, 1);

        // Execute
        List<User> result = userRepository.findByBirthdateBefore(veryOldDate);

        // Verify
        assertTrue(result.isEmpty());
    }
}