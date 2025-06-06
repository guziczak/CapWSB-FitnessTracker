package pl.wsb.fitnesstracker.statistics.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.statistics.api.Statistics;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Statistics} entities.
 * Provides CRUD operations and custom query methods for statistics data.
 */
@Repository
interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    /**
     * Finds statistics for a specific user.
     *
     * @param userId the ID of the user
     * @return an Optional containing the statistics if found, empty otherwise
     */
    Optional<Statistics> findByUserId(Long userId);

    /**
     * Finds all statistics where total calories burned is greater than the specified value.
     *
     * @param calories the minimum number of calories burned
     * @return a list of statistics matching the criteria
     */
    List<Statistics> findByTotalCaloriesBurnedGreaterThan(int calories);

    /**
     * Checks if statistics exist for a specific user.
     *
     * @param userId the ID of the user
     * @return true if statistics exist, false otherwise
     */
    boolean existsByUserId(Long userId);
}