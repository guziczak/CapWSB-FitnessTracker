package pl.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import pl.wsb.fitnesstracker.user.internal.UserDto;

import java.util.Date;

/**
 * Data Transfer Object for Training entity.
 * Used for transferring training data between layers.
 *
 * @param id the training ID (nullable for new trainings)
 * @param user the user associated with the training
 * @param userId the user ID (used when creating/updating)
 * @param startTime the training start time
 * @param endTime the training end time
 * @param activityType the type of activity
 * @param distance the distance covered in the training
 * @param averageSpeed the average speed during the training
 */
public record TrainingDto(
        @Nullable Long id,
        @Nullable UserDto user,
        @Nullable Long userId,
        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        Date startTime,
        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        Date endTime,
        @NotNull(message = "Activity type is required")
        ActivityType activityType,
        @PositiveOrZero(message = "Distance must be positive or zero")
        double distance,
        @PositiveOrZero(message = "Average speed must be positive or zero")
        double averageSpeed) {
}