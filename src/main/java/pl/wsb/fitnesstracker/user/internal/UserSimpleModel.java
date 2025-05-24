package pl.wsb.fitnesstracker.user.internal;

import org.springframework.lang.Nullable;

record UserSimpleModel (@Nullable Long id, String firstName, String lastName){
}
