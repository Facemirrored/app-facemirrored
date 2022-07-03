package de.facemirrored.backend.rest.user;

import java.util.List;
import lombok.Builder;

@Builder
public class SignUpResponse {

  private final SignUpStatus signUpStatus;

  private final String username;

  private final String email;

  private final List<String> roleList;
}
