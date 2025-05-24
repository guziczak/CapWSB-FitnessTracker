package pl.wsb.fitnesstracker.training.api;

import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingProvider {

    /**
     * Retrieves a training based on their ID.
     * If the training with given ID is not found, then {@link Optional#empty()} will be returned.
     *
     * @param trainingId id of the training to be searched
     * @return An {@link Optional} containing the located Training, or {@link Optional#empty()} if not found
     */
    Optional<Training> getTraining(Long trainingId);

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings
     */
    List<Training> findAllTrainings();

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId id of the user
     * @return list of trainings for the user
     */
    List<Training> findTrainingsByUserId(Long userId);

    /**
     * Retrieves all trainings finished after the specified time.
     *
     * @param afterTime the time after which trainings should be finished
     * @return list of trainings finished after the time
     */
    List<Training> findFinishedTrainingsAfter(Date afterTime);

    /**
     * Retrieves all trainings by activity type.
     *
     * @param activityType the activity type
     * @return list of trainings with the specified activity type
     */
    List<Training> findTrainingsByActivityType(ActivityType activityType);

    /**
     * Creates a new training.
     *
     * @param training the training to create
     * @return the created training
     */
    Training createTraining(Training training);

    /**
     * Updates an existing training.
     *
     * @param trainingId the ID of the training to update
     * @param training the training data
     * @return the updated training
     */
    Training updateTraining(Long trainingId, Training training);

}