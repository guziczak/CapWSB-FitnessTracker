package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.internal.UserDto;

@Component
@RequiredArgsConstructor
public class TrainingMapper {

    private final UserProvider userProvider;

    public TrainingDto toDto(Training training) {
        User user = training.getUser();
        UserDto userDto = new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail()
        );
        
        return new TrainingDto(
                training.getId(),
                userDto,
                user.getId(),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }

    public Training toEntity(TrainingDto dto) {
        User user = null;
        if (dto.userId() != null) {
            user = userProvider.getUser(dto.userId())
                    .orElseThrow(() -> new UserNotFoundException(dto.userId()));
        }
        
        return new Training(
                user,
                dto.startTime(),
                dto.endTime(),
                dto.activityType(),
                dto.distance(),
                dto.averageSpeed()
        );
    }
}