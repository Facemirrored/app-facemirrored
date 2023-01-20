package de.facemirrored.backend.rest.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpRequest {

  @NotNull
  @NotBlank
  private final String username;

  @NotNull
  @NotBlank
  private final String password;
}
