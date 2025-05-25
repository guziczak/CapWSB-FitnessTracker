package pl.wsb.fitnesstracker.user.internal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;
import pl.wsb.fitnesstracker.training.internal.TrainingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public User createUser(final User user) {
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByPartOfEmail(final String email) {
        return userRepository.findByPartOfEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        
        trainingRepository.deleteByUser(user);
        
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, String firstName, String lastName, String email, LocalDate birthdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (birthdate != null) user.setBirthdate(birthdate);

        userRepository.save(user);
    }

    @Override
    public List<User> getUsersOlderThan(int age) {
        LocalDate cutoffDate = LocalDate.now().minusYears(age);
        return userRepository.findByBirthdateBefore(cutoffDate);
    }
}