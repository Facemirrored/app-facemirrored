package de.facemirrored.backend.config.authentication.jwtauth;


import static java.lang.String.format;

import de.facemirrored.backend.config.authentication.springboot.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication support via JWT-Token. Including token generation, verification
 * and extraction from a given token. A JWT-Token is based on the private backend issuer and secret
 * code.
 */
@Component
public class JwtUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  public static final String INVALID_JWT_TOKEN = "Invalid JWT token: {token=%s}";
  public static final String JWT_TOKEN_IS_EXPIRED = "JWT token is expired: {token=%s}";
  public static final String JWT_TOKEN_IS_UNSUPPORTED = "JWT token is unsupported: {token=%s}";
  public static final String JWT_CLAIMS_STRING_IS_EMPTY = "JWT claims string is empty: {token=%s}";

  @Value("${facemirrored.app.issuer}")
  private String issuer;

  private SecretKey secret;

  @Value("${facemirrored.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  /**
   * Generierung eines JWT-Tokens auf Basis des bereits authentifizierten Users. Dieser ist in
   * {@link UserDetailsImpl} hinterlegt.
   *
   * @param authentication Authentifizierungsmanager-Objekt. In diesem Falle
   *                       {@link UserDetailsImpl}.
   * @return JWT-Token als String
   */
  public String generateJwtToken(final Authentication authentication) {

    final var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    this.secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    return Jwts.builder()
        .setSubject((userPrincipal.getUsername()))
        .setIssuer(this.issuer)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(this.secret)
        .compact();
  }

  /**
   * Extrahiert den Usernamen aus einem übergebenen JWT-Token.
   *
   * @param token gültiges JWT-Token
   * @return Username als String
   */
  public String getUserNameFromJwtToken(final String token) {

    return Jwts.parserBuilder()
        .setSigningKey(this.secret)
        .requireIssuer(this.issuer)
        .build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Validierung eines JWT-Tokens. Es wird geprüft, ob das Secret und der Issuer übereinstimmt. Wenn
   * eins der beiden Eigenschaften nicht gültig ist, so muss davon ausgegangen werden, dass das
   * Token nicht von diesem Service generiert worden ist.
   *
   * @param authToken Token, das validiert werden soll
   * @return Ob das Token valide ist
   */
  public boolean validateJwtToken(String authToken) {
    try {

      Jwts.parserBuilder()
          .setSigningKey(this.secret)
          .requireIssuer(this.issuer)
          .build().parseClaimsJws(authToken);

      return true;

    } catch (MalformedJwtException e) {

      logger.error(format(INVALID_JWT_TOKEN, authToken), e.getMessage());

    } catch (ExpiredJwtException e) {

      logger.error(format(JWT_TOKEN_IS_EXPIRED, authToken), e.getMessage());

    } catch (UnsupportedJwtException e) {

      logger.error(format(JWT_TOKEN_IS_UNSUPPORTED, authToken), e.getMessage());

    } catch (IllegalArgumentException e) {

      logger.error(format(JWT_CLAIMS_STRING_IS_EMPTY, authToken), e.getMessage());
    }

    return false;
  }
}