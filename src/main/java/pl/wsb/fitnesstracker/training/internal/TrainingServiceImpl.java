package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.internal.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<Training> getTraining(final Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    @Override
    public List<Training> findAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> findTrainingsByUserId(Long userId) {
        return trainingRepository.findAll().stream().filter(training -> training.getUser().getId().equals(userId)).toList();
    }

    @Override
    public List<Training> findFinishedTrainingsAfter(Date afterTime) {
        return trainingRepository.findAll().stream().filter(training -> training.getEndTime().after(afterTime)).toList();
    }

    @Override
    public List<Training> findTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findAll().stream().filter(training -> training.getActivityType().equals(activityType)).toList();
    }

    @Override
    public Training createTraining(Training training) {
        return trainingRepository.save(training);
    }

    @Override
    public Training updateTraining(Long trainingId, TrainingDTO training) {
        Training existingTraining = trainingRepository.findById(trainingId).orElseThrow(() -> new TrainingNotFoundException(trainingId));

        Long userId = training.getUserId();
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new IllegalArgumentException("User with id " + userId + " does not exists!"));
            existingTraining.setUser(user);
        }
        if (training.getStartTime() != null) {
            existingTraining.setStartTime(training.getStartTime());
        }
        if (training.getEndTime() != null) {
            existingTraining.setEndTime(training.getEndTime());
        }
        if (training.getActivityType() != null) {
            existingTraining.setActivityType(training.getActivityType());
        }
        if (!Double.isNaN(training.getDistance())) {
            existingTraining.setDistance(training.getDistance());
        }
        if (!Double.isNaN(training.getAverageSpeed())) {
            existingTraining.setAverageSpeed(training.getAverageSpeed());
        }

        return trainingRepository.save(existingTraining);
    }

}