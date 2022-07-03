package de.facemirrored.backend.service;

import de.facemirrored.backend.config.authentication.jwtauth.JwtUtils;
import de.facemirrored.backend.config.authentication.springboot.UserDetailsImpl;
import de.facemirrored.backend.database.repository.UserRepository;
import de.facemirrored.backend.rest.user.SignInRequest;
import de.facemirrored.backend.rest.user.SignInResponse;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final JwtUtils jwtUtils;

  public SignInResponse authenticateAndSignInUser(final SignInRequest signInRequest) {

    // TODO: authentication exception controlleradvice
    // authenticate user
    final var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            signInRequest.getUsername(),
            signInRequest.getPassword()));

    // save authenticated user and create jwt-token
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final var jwt = jwtUtils.generateJwtToken(authentication);

    // load roles
    final var userDetails = (UserDetailsImpl) authentication.getPrincipal();
    final var userRoles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return SignInResponse.builder()
        .token(jwt)
        .username(userDetails.getUsername())
        .email(userDetails.getEmail())
        .roleList(userRoles)
        .build();
  }
}
