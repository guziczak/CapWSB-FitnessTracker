package pl.wsb.fitnesstracker.user.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

    private final EntityManager entityManager;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/simple")
    public List<UserSimpleModel> getSimpleUser() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleUser)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return userService
                .getUser((long) id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userMapper.toDto(
                userService.createUser(userMapper.toEntity(userDto))
        );
    }

}