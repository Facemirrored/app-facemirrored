package de.facemirrored.backend.config.authentication.jwtauth;

import static java.util.Objects.nonNull;

import de.facemirrored.backend.config.authentication.services.UserDetailsServiceImpl;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter-Class processed by every request. Handles JWT-Authentication- and User-Authorization.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

  private static final String JWT_TOKEN_WARN = "JWT-Token in request is invalid. [request-uri = %s; jwt-token = %s]";

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  /**
   * Authenticate user, if contained. Username will be extracted from given JWT-Token and be
   * searched in DB via
   * {@link de.facemirrored.backend.database.repository.UserRepository UserRepository}. Found user
   * containes more specific information like roles for authorization. Given information will be set
   * in the Spring Boot
   * {@link org.springframework.security.core.Authentication Authentication Object}.
   *
   * @param request     Request-Objekt
   * @param response    Response-Objekt
   * @param filterChain Filter-Chain-Objekt
   * @throws ServletException Servlet-Exception-Objekt
   * @throws IOException      IOException-Objekt
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    final var jwt = parseJwt(request);

    if (nonNull(jwt) && jwtUtils.validateJwtToken(jwt)) {

      final var username = jwtUtils.getUserNameFromJwtToken(jwt);
      final var userDetails = userDetailsService.loadUserByUsername(username);
      final var authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } else {

      logger.warn(String.format(JWT_TOKEN_WARN, request.getRequestURI(), jwt));
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracting the JWT-Token from the authorization header, if contained and valid.
   *
   * @param request Request-Object
   * @return JWT-Token as String. Null if not contained or invalid.
   */
  private String parseJwt(HttpServletRequest request) {

    final var headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {

      return headerAuth.substring(7);
    }

    return null;
  }
}