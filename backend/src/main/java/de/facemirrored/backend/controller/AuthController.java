package de.facemirrored.backend.controller;

import de.facemirrored.backend.ui.SignInResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
public class AuthController {

  // das ist ein test kommentar
  @PostMapping(path = "/signIn", consumes = "application/json", produces = "application/json")
  public ResponseEntity<SignInResponse> authenticateUser() {
    throw new UnsupportedOperationException("not implemented");
  }

}
