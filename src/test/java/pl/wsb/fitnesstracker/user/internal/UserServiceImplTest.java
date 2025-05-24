package pl.wsb.fitnesstracker.user.internal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wsb.fitnesstracker.user.api.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = createUserWithId(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com");
        testUser2 = createUserWithId(2L, "Jane", "Smith", LocalDate.of(1985, 5, 15), "jane@example.com");
    }
    
    private User createUserWithId(Long id, String firstName, String lastName, LocalDate birthdate, String email) {
        User user = new User(firstName, lastName, birthdate, email);
        // Use reflection to set the ID since there's no setter
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
        return user;
    }

    @Test
    void shouldCreateUser() {
        User newUser = new User("Alice", "Brown", LocalDate.of(2000, 3, 20), "alice@example.com");
        User savedUser = createUserWithId(3L, "Alice", "Brown", LocalDate.of(2000, 3, 20), "alice@example.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(newUser);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Alice", result.getFirstName());
        verify(userRepository).save(newUser);
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithId() {
        User userWithId = createUserWithId(99L, "Bob", "Green", LocalDate.of(1995, 7, 10), "bob@example.com");

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userWithId));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUser(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(999L);

        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldGetUserByEmail() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldGetUsersByPartOfEmail() {
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findByPartOfEmail("example")).thenReturn(users);

        List<User> result = userService.getUsersByPartOfEmail("example");

        assertEquals(2, result.size());
        assertTrue(result.contains(testUser));
        assertTrue(result.contains(testUser2));
        verify(userRepository).findByPartOfEmail("example");
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = Arrays.asList(testUser, testUser2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void shouldDeleteUserById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUserById(999L));
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "UpdatedFirstName", "UpdatedLastName", 
                "updated@example.com", LocalDate.of(1990, 6, 15));

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals("UpdatedFirstName", testUser.getFirstName());
        assertEquals("UpdatedLastName", testUser.getLastName());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals(LocalDate.of(1990, 6, 15), testUser.getBirthdate());
    }

    @Test
    void shouldUpdateUserPartially() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, null, "OnlyLastNameUpdated", null, null);

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals("John", testUser.getFirstName()); // unchanged
        assertEquals("OnlyLastNameUpdated", testUser.getLastName());
        assertEquals("john@example.com", testUser.getEmail()); // unchanged
        assertEquals(LocalDate.of(1990, 1, 1), testUser.getBirthdate()); // unchanged
    }

    @Test
    void shouldUpdateOnlyEmailAndBirthdate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, null, null, "newemail@example.com", LocalDate.of(1991, 2, 2));

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals("John", testUser.getFirstName()); // unchanged
        assertEquals("Doe", testUser.getLastName()); // unchanged
        assertEquals("newemail@example.com", testUser.getEmail());
        assertEquals(LocalDate.of(1991, 2, 2), testUser.getBirthdate());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            userService.updateUser(999L, "Name", "Surname", "email@test.com", LocalDate.now())
        );
        
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldGetUsersOlderThan() {
        int age = 35;
        LocalDate cutoffDate = LocalDate.now().minusYears(age);
        List<User> olderUsers = Arrays.asList(testUser, testUser2);
        
        when(userRepository.findByBirthdateBefore(any(LocalDate.class))).thenReturn(olderUsers);

        List<User> result = userService.getUsersOlderThan(age);

        assertEquals(2, result.size());
        verify(userRepository).findByBirthdateBefore(argThat(date -> 
            date.isEqual(cutoffDate) || date.isAfter(cutoffDate.minusDays(1)) && date.isBefore(cutoffDate.plusDays(1))
        ));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersOlderThan() {
        when(userRepository.findByBirthdateBefore(any(LocalDate.class))).thenReturn(Arrays.asList());

        List<User> result = userService.getUsersOlderThan(100);

        assertTrue(result.isEmpty());
        verify(userRepository).findByBirthdateBefore(any(LocalDate.class));
    }

    @Test
    void shouldUpdateOnlyFirstName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, "NewFirstName", null, null, null);

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals("NewFirstName", testUser.getFirstName());
        assertEquals("Doe", testUser.getLastName()); // unchanged
        assertEquals("john@example.com", testUser.getEmail()); // unchanged
        assertEquals(LocalDate.of(1990, 1, 1), testUser.getBirthdate()); // unchanged
    }

    @Test
    void shouldNotUpdateWhenAllFieldsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String originalFirstName = testUser.getFirstName();
        String originalLastName = testUser.getLastName();
        String originalEmail = testUser.getEmail();
        LocalDate originalBirthdate = testUser.getBirthdate();

        userService.updateUser(1L, null, null, null, null);

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals(originalFirstName, testUser.getFirstName());
        assertEquals(originalLastName, testUser.getLastName());
        assertEquals(originalEmail, testUser.getEmail());
        assertEquals(originalBirthdate, testUser.getBirthdate());
    }
}