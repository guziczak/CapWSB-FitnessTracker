package pl.wsb.fitnesstracker.training.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        User testUser = createUserWithId(1L, "John", "Doe");
        testTraining = createTrainingWithId(1L, testUser, LocalDate.now(), LocalDate.now(), 
                                          ActivityType.RUNNING, 10.0, 60.0);
    }

    @Test
    void shouldGetTrainingById() {
        // Given
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));

        // When
        Optional<Training> result = trainingService.getTraining(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
        verify(trainingRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenTrainingNotFound() {
        // Given
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Training> result = trainingService.getTraining(999L);

        // Then
        assertFalse(result.isPresent());
        verify(trainingRepository).findById(999L);
    }

    @Test
    void shouldHandleNullTrainingId() {
        // Given
        when(trainingRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<Training> result = trainingService.getTraining(null);

        // Then
        assertFalse(result.isPresent());
        verify(trainingRepository).findById(null);
    }

    private User createUserWithId(Long id, String firstName, String lastName) {
        User user = new User(firstName, lastName, LocalDate.of(1990, 1, 1), firstName.toLowerCase() + "@example.com");
        setId(user, id);
        return user;
    }

    private Training createTrainingWithId(Long id, User user, LocalDate startDate, LocalDate endDate,
                                        ActivityType activityType, double distance, double duration) {
        Training training = new Training(user, startDate, endDate, activityType, distance, duration);
        setId(training, id);
        return training;
    }

    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field idField = entity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }
}