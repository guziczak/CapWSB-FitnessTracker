package pl.wsb.fitnesstracker.user.internal;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.user.api.User;

@Component
class UserMapper {

    UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail());
    }

    User toEntity(UserDto userDto) {
        return new User(
                userDto.firstName(),
                userDto.lastName(),
                userDto.birthdate(),
                userDto.email());
    }

    UserSimpleModel toSimpleUser(User user) {
        return new UserSimpleModel(user.getId(),
                user.getFirstName(),
                user.getLastName());
    }

    UserIdAndEmailRes toIdAndEmail(User user) {
        return new UserIdAndEmailRes(user.getId(), user.getEmail());
    }

}
