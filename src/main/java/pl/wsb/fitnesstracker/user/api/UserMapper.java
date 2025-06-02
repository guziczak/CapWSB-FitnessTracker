package pl.wsb.fitnesstracker.user.api;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.user.internal.UserDto;
import pl.wsb.fitnesstracker.user.internal.UserIdAndEmailRes;
import pl.wsb.fitnesstracker.user.internal.UserSimpleModel;

/**
 * Mapper for converting between User entities and DTOs.
 * Handles transformation of data between different layers.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to UserDto.
     *
     * @param user the entity to convert
     * @return the corresponding DTO
     */
    public UserDto toDto(User user) {
        return new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthdate(),
                user.getEmail());
    }

    /**
     * Converts a UserDto to User entity.
     *
     * @param userDto the DTO to convert
     * @return the corresponding entity
     */
    public User toEntity(UserDto userDto) {
        return new User(
                userDto.firstName(),
                userDto.lastName(),
                userDto.birthdate(),
                userDto.email());
    }

    /**
     * Converts a User entity to UserSimpleModel with basic info.
     *
     * @param user the entity to convert
     * @return the simple model with ID and name
     */
    public UserSimpleModel toSimpleUser(User user) {
        return new UserSimpleModel(user.getId(),
                user.getFirstName(),
                user.getLastName());
    }

    /**
     * Converts a User entity to UserIdAndEmailRes.
     *
     * @param user the entity to convert
     * @return the model with ID and email only
     */
    public UserIdAndEmailRes toIdAndEmail(User user) {
        return new UserIdAndEmailRes(user.getId(), user.getEmail());
    }
}
