package de.facemirrored.backend.controller;

import de.facemirrored.backend.rest.user.SignInRequest;
import de.facemirrored.backend.rest.user.SignInResponse;
import de.facemirrored.backend.rest.user.SignUpRequest;
import de.facemirrored.backend.rest.user.SignUpResponse;
import de.facemirrored.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
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

  /**
   * Authentifiziert den User und generiert bei erfolgreicher Authentifizierung ein JWT-Token.
   *
   * @return Response-Objekt mit User-Daten (Name / Email / JWT-Token / Rollen)
   */
  @PostMapping(path = "/signIn", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SignInResponse> authenticateAndSignInUser(
      @RequestBody final SignInRequest signInRequest) {

    return ResponseEntity.ok(authService.authenticateAndSignInUser(signInRequest));
  }


  @PostMapping(value = "/signUp", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SignUpResponse> signUpUserIfValid(
      @RequestBody final SignUpRequest signUpRequest) {

    throw new UnsupportedOperationException("Not implemented yet");
  }
}
