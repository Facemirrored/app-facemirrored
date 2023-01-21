package de.facemirrored.backend.controller;

import de.facemirrored.backend.database.repository.UserRepository;
import de.facemirrored.backend.exceptions.SignUpUserFailedException;
import de.facemirrored.backend.rest.user.SignInRequest;
import de.facemirrored.backend.rest.user.SignInResponse;
import de.facemirrored.backend.rest.user.SignUpRequest;
import de.facemirrored.backend.service.AuthService;
import de.facemirrored.backend.service.AuthService.SignInData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

  private static SignInResponse createSignInResponse(final SignInData signInData) {

    return SignInResponse.builder()
        .username(signInData.username())
        .token(signInData.jwtToken())
        .roleList(signInData.roleList())
        .build();
  }

  /**
   * Authenticate by given user information and generates an JWT-Token.
   *
   * @return SignInResponse-Object with user information and JWT-Token
   */
  @PostMapping(path = "/signIn", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SignInResponse> authenticateAndSignInUser(
      @RequestBody final SignInRequest signInRequest) {

    final var signInData = authService.authenticateAndSignInUser(
        signInRequest.getUsername(),
        signInRequest.getPassword());

    return ResponseEntity.ok(createSignInResponse(signInData));
  }

  /**
   * Validates given user information. If valid, creates a new user account and instantly logs in
   * the user.
   *
   * @param signUpRequest Sign up object
   * @return Logged-in user information
   */
  @PostMapping(value = "/signUp", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SignInResponse> signUpUserIfValid(
      @RequestBody final SignUpRequest signUpRequest) {

    if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (authService.signUpUser(signUpRequest.getUsername(), signUpRequest.getPassword())) {

      final var signInData = authService.authenticateAndSignInUser(
          signUpRequest.getUsername(),
          signUpRequest.getPassword());

      return ResponseEntity.ok(createSignInResponse(signInData));
    }

    throw new SignUpUserFailedException(signUpRequest.getUsername());
  }
}
