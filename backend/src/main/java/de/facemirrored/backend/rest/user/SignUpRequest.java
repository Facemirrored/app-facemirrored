package de.facemirrored.backend.rest.user;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpRequest {

  private final String username;

  private final String email;

  private final String password;
}
