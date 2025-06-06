package pl.wsb.fitnesstracker.statistics.internal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.exception.api.NotFoundException;
import pl.wsb.fitnesstracker.statistics.api.Statistics;
import pl.wsb.fitnesstracker.statistics.api.StatisticsMapper;
import pl.wsb.fitnesstracker.statistics.api.StatisticsProvider;
import pl.wsb.fitnesstracker.statistics.api.StatisticsService;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the StatisticsService interface.
 * Provides business logic for managing user statistics.
 */
@Service
class StatisticsServiceImpl implements StatisticsService, StatisticsProvider {

    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;
    private final UserProvider userProvider;
    private final TrainingProvider trainingProvider;

    /**
     * Constructor for StatisticsServiceImpl.
     *
     * @param statisticsRepository the statistics repository
     * @param statisticsMapper the statistics mapper
     * @param userProvider the user provider
     * @param trainingProvider the training provider
     */
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository,
                                StatisticsMapper statisticsMapper,
                                UserProvider userProvider,
                                TrainingProvider trainingProvider) {
        this.statisticsRepository = statisticsRepository;
        this.statisticsMapper = statisticsMapper;
        this.userProvider = userProvider;
        this.trainingProvider = trainingProvider;
    }

    @Override
    @Transactional
    public StatisticsDTO createStatistics(Long userId, StatisticsDTO dto) {
        User user = userProvider.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        if (statisticsRepository.existsByUserId(userId)) {
            throw new IllegalStateException("Statistics already exist for user with ID: " + userId);
        }

        Statistics statistics = new Statistics(user);
        statistics.setTotalTrainings(dto.getTotalTrainings() != null ? dto.getTotalTrainings() : 0);
        statistics.setTotalDistance(dto.getTotalDistance() != null ? dto.getTotalDistance() : 0.0);
        statistics.setTotalCaloriesBurned(dto.getTotalCaloriesBurned() != null ? dto.getTotalCaloriesBurned() : 0);

        Statistics saved = statisticsRepository.save(statistics);
        return statisticsMapper.toDto(saved);
    }

    @Override
    @Transactional
    public StatisticsDTO updateStatistics(Long userId, StatisticsDTO dto) {
        Statistics statistics = statisticsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Statistics not found for user with ID: " + userId));

        statisticsMapper.updateEntity(dto, statistics);
        Statistics updated = statisticsRepository.save(statistics);
        return statisticsMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getStatisticsByUserId(Long userId) {
        Statistics statistics = statisticsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Statistics not found for user with ID: " + userId));
        return statisticsMapper.toDto(statistics);
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsDTO getStatisticsById(Long statisticsId) {
        Statistics statistics = statisticsRepository.findById(statisticsId)
                .orElseThrow(() -> new NotFoundException("Statistics not found with ID: " + statisticsId));
        return statisticsMapper.toDto(statistics);
    }

    @Override
    @Transactional
    public void deleteStatistics(Long statisticsId) {
        if (!statisticsRepository.existsById(statisticsId)) {
            throw new NotFoundException("Statistics not found with ID: " + statisticsId);
        }
        statisticsRepository.deleteById(statisticsId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatisticsDTO> findStatisticsByCaloriesGreaterThan(int calories) {
        return statisticsRepository.findByTotalCaloriesBurnedGreaterThan(calories)
                .stream()
                .map(statisticsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StatisticsDTO recalculateStatistics(Long userId) {
        User user = userProvider.getUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        List<Training> userTrainings = trainingProvider.findTrainingsByUserId(userId);

        Statistics statistics = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> new Statistics(user));

        int totalTrainings = userTrainings.size();
        double totalDistance = userTrainings.stream()
                .mapToDouble(Training::getDistance)
                .sum();
        
        int totalCalories = calculateTotalCalories(userTrainings);

        statistics.setTotalTrainings(totalTrainings);
        statistics.setTotalDistance(totalDistance);
        statistics.setTotalCaloriesBurned(totalCalories);

        Statistics saved = statisticsRepository.save(statistics);
        return statisticsMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Statistics> getStatistics(Long statisticsId) {
        return statisticsRepository.findById(statisticsId);
    }

    /**
     * Calculates total calories burned based on trainings.
     * This is a simplified calculation based on distance and activity type.
     *
     * @param trainings the list of trainings
     * @return total calories burned
     */
    private int calculateTotalCalories(List<Training> trainings) {
        return trainings.stream()
                .mapToInt(training -> {
                    double distance = training.getDistance();
                    switch (training.getActivityType()) {
                        case RUNNING:
                            return (int) (distance * 100);
                        case CYCLING:
                            return (int) (distance * 50);
                        case WALKING:
                            return (int) (distance * 60);
                        case TENNIS:
                            return (int) (distance * 80);
                        default:
                            return (int) (distance * 70);
                    }
                })
                .sum();
    }
}