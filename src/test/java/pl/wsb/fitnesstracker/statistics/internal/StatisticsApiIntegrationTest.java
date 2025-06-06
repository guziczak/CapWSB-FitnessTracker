package pl.wsb.fitnesstracker.statistics.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.IntegrationTestBase;
import pl.wsb.fitnesstracker.statistics.api.Statistics;
import pl.wsb.fitnesstracker.training.api.ActivityType;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Statistics API.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.profiles.active=test"
})
@Transactional
class StatisticsApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Statistics testStatistics;

    @BeforeEach
    public void setUp() {
        // Create test user
        testUser = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@test.com");
        entityManager.persist(testUser);

        // Create some trainings for the user
        Training training1 = new Training(testUser, new Date(), new Date(), ActivityType.RUNNING, 10.0, 8.0);
        Training training2 = new Training(testUser, new Date(), new Date(), ActivityType.CYCLING, 20.0, 15.0);
        entityManager.persist(training1);
        entityManager.persist(training2);

        // Create statistics for the user
        testStatistics = new Statistics(testUser);
        testStatistics.setTotalTrainings(2);
        testStatistics.setTotalDistance(30.0);
        testStatistics.setTotalCaloriesBurned(1500);
        entityManager.persist(testStatistics);

        entityManager.flush();
    }

    @Test
    void createStatistics_shouldCreateNewStatistics() throws Exception {
        User newUser = new User("Jane", "Smith", LocalDate.of(1985, 5, 15), "jane.smith@test.com");
        entityManager.persist(newUser);
        entityManager.flush();

        StatisticsDTO newStatisticsDto = new StatisticsDTO();
        newStatisticsDto.setTotalTrainings(5);
        newStatisticsDto.setTotalDistance(50.0);
        newStatisticsDto.setTotalCaloriesBurned(2000);

        mockMvc.perform(post("/v1/statistics/users/{userId}", newUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStatisticsDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(newUser.getId()))
                .andExpect(jsonPath("$.totalTrainings").value(5))
                .andExpect(jsonPath("$.totalDistance").value(50.0))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(2000));
    }

    @Test
    void createStatistics_shouldFailWhenStatisticsAlreadyExist() throws Exception {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalTrainings(10);

        mockMvc.perform(post("/v1/statistics/users/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatistics_shouldUpdateExistingStatistics() throws Exception {
        StatisticsDTO updateDto = new StatisticsDTO();
        updateDto.setTotalTrainings(10);
        updateDto.setTotalDistance(100.0);
        updateDto.setTotalCaloriesBurned(5000);

        mockMvc.perform(put("/v1/statistics/users/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTrainings").value(10))
                .andExpect(jsonPath("$.totalDistance").value(100.0))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(5000));
    }

    @Test
    void getStatisticsByUserId_shouldReturnUserStatistics() throws Exception {
        mockMvc.perform(get("/v1/statistics/users/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.userEmail").value("john.doe@test.com"))
                .andExpect(jsonPath("$.totalTrainings").value(2))
                .andExpect(jsonPath("$.totalDistance").value(30.0))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(1500));
    }

    @Test
    void getStatisticsById_shouldReturnStatistics() throws Exception {
        mockMvc.perform(get("/v1/statistics/{id}", testStatistics.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testStatistics.getId()))
                .andExpect(jsonPath("$.totalTrainings").value(2));
    }

    @Test
    void deleteStatistics_shouldDeleteStatistics() throws Exception {
        mockMvc.perform(delete("/v1/statistics/{id}", testStatistics.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/v1/statistics/{id}", testStatistics.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByCaloriesGreaterThan_shouldReturnFilteredStatistics() throws Exception {
        // Create another user with lower calories
        User anotherUser = new User("Bob", "Johnson", LocalDate.of(1995, 3, 20), "bob.johnson@test.com");
        entityManager.persist(anotherUser);

        Statistics lowCaloriesStats = new Statistics(anotherUser);
        lowCaloriesStats.setTotalTrainings(1);
        lowCaloriesStats.setTotalDistance(5.0);
        lowCaloriesStats.setTotalCaloriesBurned(500);
        entityManager.persist(lowCaloriesStats);
        entityManager.flush();

        mockMvc.perform(get("/v1/statistics/calories-greater-than")
                .param("calories", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].totalCaloriesBurned").value(1500));
    }

    @Test
    void recalculateStatistics_shouldRecalculateBasedOnTrainings() throws Exception {
        mockMvc.perform(post("/v1/statistics/users/{userId}/recalculate", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTrainings").value(2))
                .andExpect(jsonPath("$.totalDistance").value(30.0))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(greaterThan(0)));
    }

    @Test
    void getStatisticsByUserId_shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/v1/statistics/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatistics_shouldReturn404WhenStatisticsNotFound() throws Exception {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setTotalTrainings(10);

        mockMvc.perform(put("/v1/statistics/users/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}