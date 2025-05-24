package pl.wsb.fitnesstracker.user.internal;

import org.springframework.lang.Nullable;

/**
 * Response model containing only user ID and email.
 * Used for email search endpoints.
 *
 * @param id the user ID
 * @param email the user's email address
 */
public record UserIdAndEmailRes(@Nullable Long id, String email) {
}
