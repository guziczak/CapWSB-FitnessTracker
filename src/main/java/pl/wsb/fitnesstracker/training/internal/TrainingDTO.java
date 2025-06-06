package pl.wsb.fitnesstracker.training.internal;

import lombok.*;
import pl.wsb.fitnesstracker.training.api.ActivityType;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TrainingDTO {
    private Long userId;
    private Date startTime;
    private Date endTime;
    private ActivityType activityType;
    private double distance;
    private double averageSpeed;
}
