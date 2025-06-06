package pl.wsb.fitnesstracker.training.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.training.api.ActivityType;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    void deleteByUser(User user);

    long countByUserAndStartTimeBetween(User user, Date start, Date end);

    List<Training> findByUserAndStartTimeBetween(User user, Date start, Date end);
    
    List<Training> findByUserId(Long userId);
    
    List<Training> findByEndTimeAfter(Date afterTime);
    
    List<Training> findByActivityType(ActivityType activityType);

}