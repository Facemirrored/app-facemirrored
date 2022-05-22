package de.facemirrored.backend.config.authentication.jwtauth;

import de.facemirrored.backend.config.authentication.services.UserDetailsServiceImpl;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter-Klasse, welche bei jedem Request prozessiert wird. Dabei wird vom Request
 */
public class AuthTokenFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  /**
   * Authentifiziert den User, sofern vorhanden und valide. User-Name wird aus den übergebenen
   * JWT-Token extrahiert und im User-Repository gesucht. Gefundener User aus den Repo besitzt
   * weitere Detail-Informationen, wie z.B. E-Mail-Adresse, codiertes Passwort und Rollen. Diese
   * Informationen werden zusammen mit den Request in das Authentication-Objekt von Spring Boot als
   * authentifizierte Person gesetzt.
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

    String jwt = parseJwt(request);

    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

      // extrahiere user-name vom jwt-Token
      final var username = jwtUtils.getUserNameFromJwtToken(jwt);
      // suche User im Repo
      final var userDetails = userDetailsService.loadUserByUsername(username);

      // erstelle Authentifizierungsobjekt auf Basis des gefundenen Users
      final var authentication = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities());

      // setze Request-Details
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // setze / speichere Authentifizierungsobjekt
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } else {

      logger.warn("Request: " + request.getRequestURI() + ":::JWT token null or invalid: " + jwt);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extrahiert das Token aus den Authorization-Header, sofern vorhanden und valide.
   *
   * @param request Request-Objekt
   * @return Token-Code als String. Null, wenn nicht vorhanden / invalide
   */
  private String parseJwt(HttpServletRequest request) {

    final var headerAuth = request.getHeader("Authorization");

    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {

      return headerAuth.substring(7);
    }

    return null;
  }
}