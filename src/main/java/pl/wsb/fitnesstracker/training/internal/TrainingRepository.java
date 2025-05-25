package pl.wsb.fitnesstracker.training.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.user.api.User;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    void deleteByUser(User user);
}