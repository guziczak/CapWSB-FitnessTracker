package pl.wsb.fitnesstracker.user.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserMapper;
import pl.wsb.fitnesstracker.user.api.UserProvider;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.util.List;

/**
 * REST Controller for managing users in the FitnessTracker application.
 * Provides CRUD operations and search functionality for users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;
    private final UserProvider userProvider;
    private final UserMapper userMapper;

    /**
     * Retrieves all users with full details.
     *
     * @return list of all users
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userProvider.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all users with basic information only (ID and name).
     *
     * @return list of users with basic info
     */
    @GetMapping("/simple")
    public List<UserSimpleModel> getSimpleUser() {
        return userProvider.findAllUsers()
                .stream()
                .map(userMapper::toSimpleUser)
                .toList();
    }

    /**
     * Retrieves a specific user by ID.
     *
     * @param id the user ID
     * @return the user details
     * @throws EntityNotFoundException if user not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userProvider
                .getUser(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    /**
     * Creates a new user.
     *
     * @param userDto the user data
     * @return the created user
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return userMapper.toDto(
                userService.createUser(userMapper.toEntity(userDto))
        );
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the user ID to delete
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

    /**
     * Searches for users by email fragment (case-insensitive).
     *
     * @param email the email fragment to search
     * @return list of users with ID and email only
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/email")
    public List<UserIdAndEmailRes> getByEmail(@RequestParam String email) {
        return userProvider.getUsersByPartOfEmail(email)
                .stream()
                .map(userMapper::toIdAndEmail)
                .toList();
    }

    /**
     * Retrieves users older than the specified age.
     *
     * @param age the minimum age
     * @return list of users older than the age
     */
    @GetMapping("/older/{age}")
    public List<UserDto> getOlderThanAge(@PathVariable int age) {
        return userProvider.getUsersOlderThan(age)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Updates an existing user.
     *
     * @param userId the user ID to update
     * @param request the update data
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long userId, @RequestBody @Valid UserDto request) {
        userService.updateUser(userId, request.firstName(), request.lastName(), 
                             request.email(), request.birthdate());
    }
}