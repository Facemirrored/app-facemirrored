package de.facemirrored.backend.service;

import de.facemirrored.backend.config.authentication.jwtauth.JwtUtils;
import de.facemirrored.backend.config.authentication.springboot.UserDetailsImpl;
import de.facemirrored.backend.database.model.ERole;
import de.facemirrored.backend.database.model.User;
import de.facemirrored.backend.database.repository.RoleRepository;
import de.facemirrored.backend.database.repository.UserRepository;
import java.util.List;
import java.util.Set;
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

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  private final JwtUtils jwtUtils;

  /**
   * Signs in a given user by his name and password. If the authentication is not possible an
   * {@link org.springframework.security.core.AuthenticationException Spring Boot
   * AuthenticationException} is thrown
   *
   * @param username Username
   * @param password Password
   * @return Sign in data
   */
  public SignInData authenticateAndSignInUser(final String username, final String password) {

    // authentifiziere User
    final var authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));

    // speicher authentifizierten User und erstelle JWT-Token
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final var jwt = jwtUtils.generateJwtToken(authentication);

    // lade Rollen
    final var userDetails = (UserDetailsImpl) authentication.getPrincipal();
    final var userRoles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    return new SignInData(jwt, userDetails.getUsername(), userRoles);
  }

  /**
   * Signs up a new user by creating a user object based on a {@link ERole simple role} and saving
   * the new user in the database.
   *
   * @param username Username
   * @param password Password
   * @return If user is successfully created. Controlled failure can only happening if
   * {@link ERole simple role} cannot be found in the Role-Repository.
   */
  public boolean signUpUser(final String username, final String password) {

    // get simple role for a new user from DB
    final var optionalRole = roleRepository.findByName(ERole.SIMPLE_USER);

    if (optionalRole.isEmpty()) {
      return false;
    }

    userRepository.save(new User(
        username,
        passwordEncoder.encode(password),
        Set.of(optionalRole.get())));

    return true;
  }

  public record SignInData(String jwtToken, String username, List<String> roleList) {

  }
}
