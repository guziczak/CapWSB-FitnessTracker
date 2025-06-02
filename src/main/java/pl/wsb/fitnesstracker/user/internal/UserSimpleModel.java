package pl.wsb.fitnesstracker.user.internal;

import org.springframework.lang.Nullable;

/**
 * Simple model containing only basic user information.
 * Used for endpoints that return minimal user data.
 *
 * @param id the user ID
 * @param firstName the user's first name
 * @param lastName the user's last name
 */
public record UserSimpleModel(@Nullable Long id, String firstName, String lastName) {
}
