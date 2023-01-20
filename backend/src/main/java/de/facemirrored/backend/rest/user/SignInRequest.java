package de.facemirrored.backend.rest.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInRequest {

  @NotBlank
  @NotNull
  private final String username;

  @NotBlank
  @NotNull
  private final String password;
}
