package pl.wsb.fitnesstracker.statistics.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pl.wsb.fitnesstracker.statistics.api.Statistics;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StatisticsRepository.
 */
@DataJpaTest
class StatisticsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StatisticsRepository statisticsRepository;

    private User testUser1;
    private User testUser2;
    private Statistics statistics1;
    private Statistics statistics2;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        testUser2 = new User("Jane", "Smith", LocalDate.of(1985, 5, 15), "jane.smith@example.com");
        
        testUser1 = entityManager.persistAndFlush(testUser1);
        testUser2 = entityManager.persistAndFlush(testUser2);

        // Create test statistics
        statistics1 = new Statistics(testUser1);
        statistics1.setTotalTrainings(10);
        statistics1.setTotalDistance(100.5);
        statistics1.setTotalCaloriesBurned(2500);

        statistics2 = new Statistics(testUser2);
        statistics2.setTotalTrainings(5);
        statistics2.setTotalDistance(50.0);
        statistics2.setTotalCaloriesBurned(1000);

        statistics1 = entityManager.persistAndFlush(statistics1);
        statistics2 = entityManager.persistAndFlush(statistics2);
        
        entityManager.clear();
    }

    @Test
    void findByUserId_shouldReturnStatisticsForUser() {
        Optional<Statistics> result = statisticsRepository.findByUserId(testUser1.getId());

        assertTrue(result.isPresent());
        assertEquals(statistics1.getId(), result.get().getId());
        assertEquals(testUser1.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUserId_shouldReturnEmptyWhenNoStatistics() {
        Optional<Statistics> result = statisticsRepository.findByUserId(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByTotalCaloriesBurnedGreaterThan_shouldReturnMatchingStatistics() {
        List<Statistics> result = statisticsRepository.findByTotalCaloriesBurnedGreaterThan(1500);

        assertEquals(1, result.size());
        assertEquals(statistics1.getId(), result.get(0).getId());
        assertEquals(2500, result.get(0).getTotalCaloriesBurned());
    }

    @Test
    void findByTotalCaloriesBurnedGreaterThan_shouldReturnEmptyListWhenNoMatches() {
        List<Statistics> result = statisticsRepository.findByTotalCaloriesBurnedGreaterThan(3000);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByTotalCaloriesBurnedGreaterThan_shouldReturnAllMatchingStatistics() {
        List<Statistics> result = statisticsRepository.findByTotalCaloriesBurnedGreaterThan(500);

        assertEquals(2, result.size());
    }

    @Test
    void existsByUserId_shouldReturnTrueWhenStatisticsExist() {
        boolean exists = statisticsRepository.existsByUserId(testUser1.getId());

        assertTrue(exists);
    }

    @Test
    void existsByUserId_shouldReturnFalseWhenStatisticsDoNotExist() {
        boolean exists = statisticsRepository.existsByUserId(999L);

        assertFalse(exists);
    }

    @Test
    void save_shouldPersistNewStatistics() {
        User newUser = new User("Test", "User", LocalDate.of(1995, 3, 20), "test.user@example.com");
        newUser = entityManager.persistAndFlush(newUser);

        Statistics newStatistics = new Statistics(newUser);
        newStatistics.setTotalTrainings(15);
        newStatistics.setTotalDistance(150.0);
        newStatistics.setTotalCaloriesBurned(3000);

        Statistics saved = statisticsRepository.save(newStatistics);
        entityManager.flush();

        assertNotNull(saved.getId());
        assertEquals(15, saved.getTotalTrainings());
        assertEquals(150.0, saved.getTotalDistance());
        assertEquals(3000, saved.getTotalCaloriesBurned());
    }

    @Test
    void delete_shouldRemoveStatistics() {
        statisticsRepository.deleteById(statistics1.getId());
        entityManager.flush();

        Optional<Statistics> result = statisticsRepository.findById(statistics1.getId());
        assertFalse(result.isPresent());
    }
}