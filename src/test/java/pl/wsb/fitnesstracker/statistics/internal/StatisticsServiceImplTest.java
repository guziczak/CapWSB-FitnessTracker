package pl.wsb.fitnesstracker.statistics.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.fitnesstracker.exception.api.NotFoundException;
import pl.wsb.fitnesstracker.statistics.api.Statistics;
import pl.wsb.fitnesstracker.statistics.api.StatisticsMapper;
import pl.wsb.fitnesstracker.training.api.ActivityType;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StatisticsServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private StatisticsMapper statisticsMapper;

    @Mock
    private UserProvider userProvider;

    @Mock
    private TrainingProvider trainingProvider;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private User testUser;
    private Statistics testStatistics;
    private StatisticsDTO testDto;

    @BeforeEach
    void setUp() {
        testUser = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        testUser.setId(1L);

        testStatistics = new Statistics(testUser);
        testStatistics.setId(1L);
        testStatistics.setTotalTrainings(5);
        testStatistics.setTotalDistance(50.0);
        testStatistics.setTotalCaloriesBurned(1500);

        testDto = new StatisticsDTO();
        testDto.setId(1L);
        testDto.setUserId(1L);
        testDto.setUserEmail("john.doe@example.com");
        testDto.setTotalTrainings(5);
        testDto.setTotalDistance(50.0);
        testDto.setTotalCaloriesBurned(1500);
    }

    @Test
    void createStatistics_shouldCreateNewStatistics() {
        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(statisticsRepository.existsByUserId(1L)).thenReturn(false);
        when(statisticsRepository.save(any(Statistics.class))).thenReturn(testStatistics);
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        StatisticsDTO result = statisticsService.createStatistics(1L, testDto);

        assertNotNull(result);
        assertEquals(testDto.getId(), result.getId());
        verify(statisticsRepository).save(any(Statistics.class));
    }

    @Test
    void createStatistics_shouldThrowExceptionWhenUserNotFound() {
        when(userProvider.getUser(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statisticsService.createStatistics(1L, testDto));
    }

    @Test
    void createStatistics_shouldThrowExceptionWhenStatisticsAlreadyExist() {
        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(statisticsRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> statisticsService.createStatistics(1L, testDto));
    }

    @Test
    void updateStatistics_shouldUpdateExistingStatistics() {
        when(statisticsRepository.findByUserId(1L)).thenReturn(Optional.of(testStatistics));
        when(statisticsRepository.save(testStatistics)).thenReturn(testStatistics);
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        StatisticsDTO result = statisticsService.updateStatistics(1L, testDto);

        assertNotNull(result);
        verify(statisticsMapper).updateEntity(testDto, testStatistics);
        verify(statisticsRepository).save(testStatistics);
    }

    @Test
    void updateStatistics_shouldThrowExceptionWhenStatisticsNotFound() {
        when(statisticsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statisticsService.updateStatistics(1L, testDto));
    }

    @Test
    void getStatisticsByUserId_shouldReturnStatistics() {
        when(statisticsRepository.findByUserId(1L)).thenReturn(Optional.of(testStatistics));
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        StatisticsDTO result = statisticsService.getStatisticsByUserId(1L);

        assertNotNull(result);
        assertEquals(testDto.getId(), result.getId());
    }

    @Test
    void getStatisticsById_shouldReturnStatistics() {
        when(statisticsRepository.findById(1L)).thenReturn(Optional.of(testStatistics));
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        StatisticsDTO result = statisticsService.getStatisticsById(1L);

        assertNotNull(result);
        assertEquals(testDto.getId(), result.getId());
    }

    @Test
    void deleteStatistics_shouldDeleteExistingStatistics() {
        when(statisticsRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> statisticsService.deleteStatistics(1L));
        verify(statisticsRepository).deleteById(1L);
    }

    @Test
    void deleteStatistics_shouldThrowExceptionWhenStatisticsNotFound() {
        when(statisticsRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> statisticsService.deleteStatistics(1L));
    }

    @Test
    void findStatisticsByCaloriesGreaterThan_shouldReturnFilteredStatistics() {
        List<Statistics> statisticsList = Arrays.asList(testStatistics);
        when(statisticsRepository.findByTotalCaloriesBurnedGreaterThan(1000)).thenReturn(statisticsList);
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        List<StatisticsDTO> result = statisticsService.findStatisticsByCaloriesGreaterThan(1000);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDto.getId(), result.get(0).getId());
    }

    @Test
    void recalculateStatistics_shouldCalculateAndUpdateStatistics() {
        Training training1 = new Training(testUser, new Date(), new Date(), ActivityType.RUNNING, 10.0, 8.0);
        Training training2 = new Training(testUser, new Date(), new Date(), ActivityType.CYCLING, 20.0, 15.0);
        List<Training> trainings = Arrays.asList(training1, training2);

        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(trainingProvider.findTrainingsByUserId(1L)).thenReturn(trainings);
        when(statisticsRepository.findByUserId(1L)).thenReturn(Optional.of(testStatistics));
        when(statisticsRepository.save(any(Statistics.class))).thenReturn(testStatistics);
        when(statisticsMapper.toDto(testStatistics)).thenReturn(testDto);

        StatisticsDTO result = statisticsService.recalculateStatistics(1L);

        assertNotNull(result);
        verify(statisticsRepository).save(any(Statistics.class));
    }

    @Test
    void recalculateStatistics_shouldCreateNewStatisticsIfNotExist() {
        Training training = new Training(testUser, new Date(), new Date(), ActivityType.RUNNING, 10.0, 8.0);
        List<Training> trainings = Arrays.asList(training);

        when(userProvider.getUser(1L)).thenReturn(Optional.of(testUser));
        when(trainingProvider.findTrainingsByUserId(1L)).thenReturn(trainings);
        when(statisticsRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(statisticsRepository.save(any(Statistics.class))).thenReturn(testStatistics);
        when(statisticsMapper.toDto(any(Statistics.class))).thenReturn(testDto);

        StatisticsDTO result = statisticsService.recalculateStatistics(1L);

        assertNotNull(result);
        verify(statisticsRepository).save(any(Statistics.class));
    }

    @Test
    void getStatistics_shouldReturnStatisticsFromProvider() {
        when(statisticsRepository.findById(1L)).thenReturn(Optional.of(testStatistics));

        Optional<Statistics> result = statisticsService.getStatistics(1L);

        assertTrue(result.isPresent());
        assertEquals(testStatistics, result.get());
    }
}