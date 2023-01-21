package de.facemirrored.backend.rest.user;

import java.util.List;
import lombok.Builder;

@Builder
public class SignInResponse {

  private final String token;

  private final String username;

  private final List<String> roleList;
}
