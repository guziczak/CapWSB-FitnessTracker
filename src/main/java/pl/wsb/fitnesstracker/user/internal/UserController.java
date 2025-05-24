package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

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
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        return userService
                .getUser((long) id)
                .map(userMapper::toDto)
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        userService.deleteUserById((long) userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/email")
    public List<UserIdAndEmailRes> getByEmail(@RequestParam String email) {
        return userService.getUsersByPartOfEmail(email)
                .stream()
                .map(userMapper::toIdAndEmail)
                .toList();
    }

    @GetMapping("/older/{time}")
    public List<UserDto> getOlderThanAge(@PathVariable LocalDate time) {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .filter(el -> el.birthdate().isBefore(time))
                .toList();
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long userId, @RequestBody UserDto request) {
        userService.updateUser(userId, request);
    }


}