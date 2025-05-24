package pl.wsb.fitnesstracker.training.internal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;

import java.util.Date;
import java.util.List;

/**
 * REST Controller for managing trainings in the FitnessTracker application.
 * Provides endpoints for retrieving training data.
 */
@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
class TrainingController {

    private final TrainingRepository trainingRepository;
    private final UserProvider userProvider;

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings
     */
    @GetMapping
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId the user ID
     * @return list of trainings for the user
     */
    @GetMapping("/{userId}")
    public List<Training> getTrainingsByUserId(@PathVariable Long userId) {
        return trainingRepository.findByUserId(userId);
    }

    /**
     * Retrieves trainings by activity type.
     *
     * @param activityType the activity type
     * @return list of trainings with the specified activity type
     */
    @GetMapping("/activityType")
    public List<Training> getTrainingsByActivityType(@RequestParam ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    /**
     * Retrieves trainings finished after the specified time.
     *
     * @param afterTime the time threshold
     * @return list of trainings finished after the time
     */
    @GetMapping("/finished/{afterTime}")
    public List<Training> getTrainingsFinishedAfter(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date afterTime) {
        return trainingRepository.findByEndTimeAfter(afterTime);
    }

    /**
     * Creates a new training.
     *
     * @param trainingDto the training data
     * @return the created training
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Training createTraining(@RequestBody @Valid TrainingDto trainingDto) {
        User user = userProvider.getUser(trainingDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + trainingDto.userId() + " not found"));
        
        Training training = new Training(
                user,
                trainingDto.startTime(),
                trainingDto.endTime(),
                trainingDto.activityType(),
                trainingDto.distance(),
                trainingDto.averageSpeed()
        );
        
        return trainingRepository.save(training);
    }

    /**
     * Updates an existing training.
     *
     * @param trainingId the training ID to update
     * @param trainingDto the new training data
     * @return the updated training
     */
    @PutMapping("/{trainingId}")
    @ResponseStatus(HttpStatus.OK)
    public Training updateTraining(@PathVariable Long trainingId, @RequestBody @Valid TrainingDto trainingDto) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training with ID " + trainingId + " not found"));
        
        User user = userProvider.getUser(trainingDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + trainingDto.userId() + " not found"));
        
        training.setUser(user);
        training.setStartTime(trainingDto.startTime());
        training.setEndTime(trainingDto.endTime());
        training.setActivityType(trainingDto.activityType());
        training.setDistance(trainingDto.distance());
        training.setAverageSpeed(trainingDto.averageSpeed());
        
        return trainingRepository.save(training);
    }
}