package pl.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.wsb.fitnesstracker.exception.api.GlobalExceptionHandler;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserProvider userProvider;

    @InjectMocks
    private TrainingController trainingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private Training testTraining;
    private TrainingDto testTrainingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUser = createUserWithId(1L, "John", "Doe");
        testTraining = createTrainingWithId(1L, testUser, LocalDate.now().minusDays(1), LocalDate.now(),
                ActivityType.RUNNING, 10.0, 30.0);
        testTrainingDto = new TrainingDto(1L, LocalDate.now().minusDays(1), LocalDate.now(),
                ActivityType.RUNNING, 10.0, 30.0);
    }

    @Test
    void shouldGetAllTrainings() throws Exception {
        List<Training> trainings = Arrays.asList(testTraining);
        when(trainingRepository.findAll()).thenReturn(trainings);

        mockMvc.perform(get("/v1/trainings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(trainingRepository).findAll();
    }

    @Test
    void shouldGetTrainingsByUserId() throws Exception {
        List<Training> trainings = Arrays.asList(testTraining);
        when(trainingRepository.findByUserId(1L)).thenReturn(trainings);

        mockMvc.perform(get("/v1/trainings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(trainingRepository).findByUserId(1L);
    }

    @Test
    void shouldGetTrainingsByActivityType() throws Exception {
        List<Training> trainings = Arrays.asList(testTraining);
        when(trainingRepository.findByActivityType(ActivityType.RUNNING)).thenReturn(trainings);

        mockMvc.perform(get("/v1/trainings/activityType")
                        .param("activityType", "RUNNING"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(trainingRepository).findByActivityType(ActivityType.RUNNING);
    }

    @Test
    void shouldGetTrainingsFinishedAfter() throws Exception {
        List<Training> trainings = Arrays.asList(testTraining);
        when(trainingRepository.findByEndTimeAfter(any(Date.class))).thenReturn(trainings);

        mockMvc.perform(get("/v1/trainings/finished/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(trainingRepository).findByEndTimeAfter(any(Date.class));
    }

    @Test
    void shouldCreateTraining() throws Exception {
        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        mockMvc.perform(post("/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainingDto)))
                .andExpect(status().isCreated());

        verify(userProvider).getUser(1L);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTrainingWithNonExistentUser() throws Exception {
        when(userProvider.getUser(999L)).thenReturn(Optional.empty());

        TrainingDto invalidDto = new TrainingDto(999L, LocalDate.now().minusDays(1), LocalDate.now(),
                ActivityType.RUNNING, 10.0, 30.0);

        mockMvc.perform(post("/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("User with ID 999 not found",
                        result.getResolvedException().getMessage()));

        verify(userProvider).getUser(999L);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void shouldUpdateTraining() throws Exception {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));
        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(trainingRepository.save(any(Training.class))).thenReturn(testTraining);

        mockMvc.perform(put("/v1/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainingDto)))
                .andExpect(status().isOk());

        verify(trainingRepository).findById(1L);
        verify(userProvider).getUser(1L);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTraining() throws Exception {
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/trainings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrainingDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("Training with ID 999 not found",
                        result.getResolvedException().getMessage()));

        verify(trainingRepository).findById(999L);
        verify(userProvider, never()).getUser(anyLong());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTrainingWithNonExistentUser() throws Exception {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));
        when(userProvider.getUser(999L)).thenReturn(Optional.empty());

        TrainingDto invalidDto = new TrainingDto(999L, LocalDate.now().minusDays(1), LocalDate.now(),
                ActivityType.RUNNING, 10.0, 30.0);

        mockMvc.perform(put("/v1/trainings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
                .andExpect(result -> assertEquals("User with ID 999 not found",
                        result.getResolvedException().getMessage()));

        verify(trainingRepository).findById(1L);
        verify(userProvider).getUser(999L);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void shouldValidateTrainingDto() throws Exception {
        // Invalid DTO with null values
        String invalidJson = "{ \"userId\": null, \"startTime\": null, \"endTime\": null, \"activityType\": null }";

        mockMvc.perform(post("/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(trainingRepository, never()).save(any());
    }

    private User createUserWithId(Long id, String firstName, String lastName) {
        User user = new User(firstName, lastName, LocalDate.of(1990, 1, 1), firstName.toLowerCase() + "@example.com");
        setId(user, id);
        return user;
    }

    private Training createTrainingWithId(Long id, User user, LocalDate startDate, LocalDate endDate,
                                        ActivityType activityType, double distance, double avgSpeed) {
        Training training = new Training(user, startDate, endDate, activityType, distance, avgSpeed);
        setId(training, id);
        return training;
    }

    private void setId(Object entity, Long id) {
        try {
            Class<?> clazz = entity.getClass();
            // For Training, the id field is in the superclass
            if (entity instanceof Training) {
                clazz = clazz.getSuperclass();
            }
            java.lang.reflect.Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }
}