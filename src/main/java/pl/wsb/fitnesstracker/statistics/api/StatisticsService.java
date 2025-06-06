package pl.wsb.fitnesstracker.statistics.api;

import pl.wsb.fitnesstracker.statistics.internal.StatisticsDTO;

import java.util.List;

/**
 * Service interface for managing statistics.
 * Provides methods for CRUD operations and business logic related to statistics.
 */
public interface StatisticsService {

    /**
     * Creates new statistics for a user.
     *
     * @param userId the ID of the user
     * @param dto the statistics data
     * @return the created statistics as DTO
     */
    StatisticsDTO createStatistics(Long userId, StatisticsDTO dto);

    /**
     * Updates existing statistics for a user.
     *
     * @param userId the ID of the user
     * @param dto the updated statistics data
     * @return the updated statistics as DTO
     */
    StatisticsDTO updateStatistics(Long userId, StatisticsDTO dto);

    /**
     * Gets statistics for a specific user.
     *
     * @param userId the ID of the user
     * @return the statistics as DTO
     */
    StatisticsDTO getStatisticsByUserId(Long userId);

    /**
     * Gets statistics by ID.
     *
     * @param statisticsId the ID of the statistics
     * @return the statistics as DTO
     */
    StatisticsDTO getStatisticsById(Long statisticsId);

    /**
     * Deletes statistics by ID.
     *
     * @param statisticsId the ID of the statistics to delete
     */
    void deleteStatistics(Long statisticsId);

    /**
     * Finds all statistics where calories burned is greater than the specified value.
     *
     * @param calories the minimum number of calories
     * @return list of statistics matching the criteria
     */
    List<StatisticsDTO> findStatisticsByCaloriesGreaterThan(int calories);

    /**
     * Recalculates statistics for a specific user based on their trainings.
     *
     * @param userId the ID of the user
     * @return the updated statistics as DTO
     */
    StatisticsDTO recalculateStatistics(Long userId);
}