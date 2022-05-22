package de.facemirrored.backend.rest.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInRequest {

  private final String username;

  private final String password;

  private final String email;
}
