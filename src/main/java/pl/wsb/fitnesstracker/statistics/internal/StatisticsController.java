package pl.wsb.fitnesstracker.statistics.internal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.statistics.api.StatisticsService;

import java.util.List;

/**
 * REST controller for managing statistics.
 * Provides endpoints for CRUD operations on user statistics.
 */
@RestController
@RequestMapping("/v1/statistics")
class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Constructor for StatisticsController.
     *
     * @param statisticsService the statistics service
     */
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Creates new statistics for a user.
     *
     * @param userId the ID of the user
     * @param dto the statistics data
     * @return the created statistics
     */
    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticsDTO createStatistics(@PathVariable Long userId, @RequestBody StatisticsDTO dto) {
        return statisticsService.createStatistics(userId, dto);
    }

    /**
     * Updates statistics for a user.
     *
     * @param userId the ID of the user
     * @param dto the updated statistics data
     * @return the updated statistics
     */
    @PutMapping("/users/{userId}")
    public StatisticsDTO updateStatistics(@PathVariable Long userId, @RequestBody StatisticsDTO dto) {
        return statisticsService.updateStatistics(userId, dto);
    }

    /**
     * Gets statistics for a specific user.
     *
     * @param userId the ID of the user
     * @return the user's statistics
     */
    @GetMapping("/users/{userId}")
    public StatisticsDTO getStatisticsByUserId(@PathVariable Long userId) {
        return statisticsService.getStatisticsByUserId(userId);
    }

    /**
     * Gets statistics by ID.
     *
     * @param statisticsId the ID of the statistics
     * @return the statistics
     */
    @GetMapping("/{statisticsId}")
    public StatisticsDTO getStatisticsById(@PathVariable Long statisticsId) {
        return statisticsService.getStatisticsById(statisticsId);
    }

    /**
     * Deletes statistics by ID.
     *
     * @param statisticsId the ID of the statistics to delete
     */
    @DeleteMapping("/{statisticsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStatistics(@PathVariable Long statisticsId) {
        statisticsService.deleteStatistics(statisticsId);
    }

    /**
     * Finds all statistics where calories burned is greater than the specified value.
     *
     * @param calories the minimum number of calories
     * @return list of statistics matching the criteria
     */
    @GetMapping("/calories-greater-than")
    public List<StatisticsDTO> findByCaloriesGreaterThan(@RequestParam int calories) {
        return statisticsService.findStatisticsByCaloriesGreaterThan(calories);
    }

    /**
     * Recalculates statistics for a user based on their trainings.
     *
     * @param userId the ID of the user
     * @return the recalculated statistics
     */
    @PostMapping("/users/{userId}/recalculate")
    public StatisticsDTO recalculateStatistics(@PathVariable Long userId) {
        return statisticsService.recalculateStatistics(userId);
    }
}