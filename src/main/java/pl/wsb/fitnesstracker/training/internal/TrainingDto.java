package pl.wsb.fitnesstracker.training.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Date;

/**
 * Data Transfer Object for creating/updating trainings.
 */
public record TrainingDto(
        @NotNull Long userId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull Date startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull Date endTime,
        @NotNull ActivityType activityType,
        @PositiveOrZero double distance,
        @PositiveOrZero double averageSpeed) {
}