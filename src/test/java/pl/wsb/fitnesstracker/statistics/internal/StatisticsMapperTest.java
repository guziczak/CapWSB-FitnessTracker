package pl.wsb.fitnesstracker.statistics.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.wsb.fitnesstracker.statistics.api.Statistics;
import pl.wsb.fitnesstracker.statistics.api.StatisticsMapper;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StatisticsMapper.
 */
class StatisticsMapperTest {

    private StatisticsMapper statisticsMapper;
    private User testUser;
    private Statistics testStatistics;

    @BeforeEach
    void setUp() {
        statisticsMapper = new StatisticsMapper();
        
        testUser = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        testUser.setId(1L);
        
        testStatistics = new Statistics(testUser);
        testStatistics.setId(1L);
        testStatistics.setTotalTrainings(10);
        testStatistics.setTotalDistance(100.5);
        testStatistics.setTotalCaloriesBurned(2500);
    }

    @Test
    void toDto_shouldConvertStatisticsToDto() {
        StatisticsDTO dto = statisticsMapper.toDto(testStatistics);

        assertNotNull(dto);
        assertEquals(testStatistics.getId(), dto.getId());
        assertEquals(testUser.getId(), dto.getUserId());
        assertEquals(testUser.getEmail(), dto.getUserEmail());
        assertEquals(testStatistics.getTotalTrainings(), dto.getTotalTrainings());
        assertEquals(testStatistics.getTotalDistance(), dto.getTotalDistance());
        assertEquals(testStatistics.getTotalCaloriesBurned(), dto.getTotalCaloriesBurned());
    }

    @Test
    void toDto_shouldReturnNullForNullInput() {
        StatisticsDTO dto = statisticsMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_shouldHandleNullUser() {
        testStatistics.setUser(null);
        
        StatisticsDTO dto = statisticsMapper.toDto(testStatistics);

        assertNotNull(dto);
        assertNull(dto.getUserId());
        assertNull(dto.getUserEmail());
    }

    @Test
    void updateEntity_shouldUpdateStatisticsFromDto() {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalTrainings(20);
        dto.setTotalDistance(200.0);
        dto.setTotalCaloriesBurned(5000);

        statisticsMapper.updateEntity(dto, testStatistics);

        assertEquals(20, testStatistics.getTotalTrainings());
        assertEquals(200.0, testStatistics.getTotalDistance());
        assertEquals(5000, testStatistics.getTotalCaloriesBurned());
    }

    @Test
    void updateEntity_shouldOnlyUpdateNonNullFields() {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalTrainings(30);
        // Leave other fields null

        int originalDistance = (int) testStatistics.getTotalDistance();
        int originalCalories = testStatistics.getTotalCaloriesBurned();

        statisticsMapper.updateEntity(dto, testStatistics);

        assertEquals(30, testStatistics.getTotalTrainings());
        assertEquals(originalDistance, (int) testStatistics.getTotalDistance());
        assertEquals(originalCalories, testStatistics.getTotalCaloriesBurned());
    }

    @Test
    void updateEntity_shouldHandleNullDto() {
        int originalTrainings = testStatistics.getTotalTrainings();
        double originalDistance = testStatistics.getTotalDistance();
        int originalCalories = testStatistics.getTotalCaloriesBurned();

        statisticsMapper.updateEntity(null, testStatistics);

        assertEquals(originalTrainings, testStatistics.getTotalTrainings());
        assertEquals(originalDistance, testStatistics.getTotalDistance());
        assertEquals(originalCalories, testStatistics.getTotalCaloriesBurned());
    }

    @Test
    void updateEntity_shouldHandleNullStatistics() {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalTrainings(20);

        // Should not throw exception
        assertDoesNotThrow(() -> statisticsMapper.updateEntity(dto, null));
    }
}