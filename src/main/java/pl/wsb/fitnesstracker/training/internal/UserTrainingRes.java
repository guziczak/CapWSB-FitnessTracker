package pl.wsb.fitnesstracker.training.internal;

import lombok.*;
import pl.wsb.fitnesstracker.training.api.ActivityType;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserTrainingRes {

    private UserRes user;
    private Date startTime;
    private Date endTime;
    private ActivityType activityType;
    private double distance;
    private double averageSpeed;

    @Value
    @AllArgsConstructor()
    public static class UserRes {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
