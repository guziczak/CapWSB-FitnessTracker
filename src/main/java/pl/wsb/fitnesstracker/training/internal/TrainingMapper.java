package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserProvider;

@Component
@RequiredArgsConstructor
public class TrainingMapper {

    private final UserProvider userProvider;

    public TrainingDTO toDto(Training training) {
        User user = training.getUser();
        return new TrainingDTO(
                user.getId(),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }

    public Training toEntity(TrainingDTO dto) {
        User user = userProvider.getUser(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        return new Training(
                user,
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getActivityType(),
                dto.getDistance(),
                dto.getAverageSpeed()
        );
    }

    public UserTrainingRes toUserTrainingRes(Training training) {
        User user = training.getUser();
        return new UserTrainingRes(
                new UserTrainingRes.UserRes(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                ),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }
}