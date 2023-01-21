package de.facemirrored.backend.exceptions;

import de.facemirrored.backend.database.model.ERole;
import lombok.Getter;

/**
 * Exception which should be thrown if a user sign up is not possible. This can only happen if
 * access to the Role-Repository was not successfully because the {@link ERole simple role} was not
 * found.
 */
@Getter
public class SignUpUserFailedException extends RuntimeException {

  private static final String ERR_MSG = "Failed to sign up user due access to Role-Repository to load simple user role. [username = %s]";

  private final String username;

  public SignUpUserFailedException(final String username) {
    super(String.format(ERR_MSG, username));
    this.username = username;
  }
}
