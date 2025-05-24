package pl.wsb.fitnesstracker.training.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.training.api.Training;

import java.util.Date;
import java.util.List;

/**
 * Repository for Training entities.
 * Provides methods for accessing training data.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    /**
     * Finds all trainings for a specific user.
     *
     * @param userId the user ID
     * @return list of trainings for the user
     */
    @Query("SELECT t FROM Training t WHERE t.user.id = :userId")
    List<Training> findByUserId(@Param("userId") Long userId);

    /**
     * Finds trainings by activity type.
     *
     * @param activityType the activity type
     * @return list of trainings with the specified activity type
     */
    List<Training> findByActivityType(ActivityType activityType);

    /**
     * Finds trainings finished after the specified time.
     *
     * @param endTime the time threshold
     * @return list of trainings finished after the time
     */
    List<Training> findByEndTimeAfter(Date endTime);
}
