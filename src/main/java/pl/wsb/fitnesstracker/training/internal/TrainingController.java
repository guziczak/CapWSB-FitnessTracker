package pl.wsb.fitnesstracker.training.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * REST Controller for managing trainings in the FitnessTracker application.
 * Provides CRUD operations and search functionality for trainings.
 */
@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingProvider trainingProvider;
    private final TrainingMapper trainingMapper;

    /**
     * Retrieves all trainings.
     *
     * @return list of all trainings
     */
    @GetMapping
    public List<TrainingDTO> getAllTrainings() {
        return trainingProvider.findAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId the user ID
     * @return list of trainings for the user
     */
    @GetMapping("/{userId}")
    public List<TrainingDTO> getTrainingsByUserId(@PathVariable Long userId) {
        return trainingProvider.findTrainingsByUserId(userId)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings finished after the specified time.
     *
     * @param afterTime the time after which trainings should be finished
     * @return list of trainings finished after the time
     */
    @GetMapping("/finished/{afterTime}")
    public List<TrainingDTO> getFinishedTrainingsAfter(@PathVariable String afterTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(afterTime);
        
        return trainingProvider.findFinishedTrainingsAfter(date)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all trainings by activity type.
     *
     * @param activityType the activity type
     * @return list of trainings with the specified activity type
     */
    @GetMapping("/activityType")
    public List<TrainingDTO> getTrainingsByActivityType(@RequestParam ActivityType activityType) {
        return trainingProvider.findTrainingsByActivityType(activityType)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Creates a new training.
     *
     * @param trainingDto the training data
     * @return the created training
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingDTO createTraining(@RequestBody @Valid TrainingDTO trainingDto) {
        Training training = trainingMapper.toEntity(trainingDto);
        Training createdTraining = trainingProvider.createTraining(training);
        return trainingMapper.toDto(createdTraining);
    }

    /**
     * Updates an existing training.
     *
     * @param trainingId the training ID to update
     * @param trainingDto the training data
     * @return the updated training
     */
    @PutMapping("/{trainingId}")
    public UserTrainingRes updateTraining(@PathVariable Long trainingId, @RequestBody @Valid TrainingDTO trainingDto) {
        Training updatedTraining = trainingProvider.updateTraining(trainingId, trainingDto);
        return trainingMapper.toUserTrainingRes(updatedTraining);
    }
}