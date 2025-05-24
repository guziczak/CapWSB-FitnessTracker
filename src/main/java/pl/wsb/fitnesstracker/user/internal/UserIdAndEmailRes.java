package pl.wsb.fitnesstracker.user.internal;

import org.springframework.lang.Nullable;

public record UserIdAndEmailRes(@Nullable Long id, String email) {
}
