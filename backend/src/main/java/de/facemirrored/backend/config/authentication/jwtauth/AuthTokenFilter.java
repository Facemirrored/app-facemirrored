package de.facemirrored.backend.config.authentication.jwtauth;

import de.facemirrored.backend.config.authentication.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter-Klasse, welche bei jedem Request prozessiert wird. Dabei wird vom Request
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Authenticate and validate user, if contained. User name will be extracted from given jwt-token and be searched in user-repository.
     * Found user contains more details like e-mail / encoded password and roles.
     * This information will be set as current authentication object managed by spring boot.
     *
     * @param request     Request-object
     * @param response    Response-object
     * @param filterChain Filter-Chain-object
     * @throws ServletException Servlet-Exception-object
     * @throws IOException      IOException-objekt
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwt = parseJwt(request);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

            // extract username from jwt token
            final var username = jwtUtils.getUserNameFromJwtToken(jwt);
            // suche user im repo
            final var userDetails = userDetailsService.loadUserByUsername(username);

            // create authentication-object based on found user
            final var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            // set request details
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // save authentication-object
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {

            logger.warn("Request: " + request.getRequestURI() + ":::JWT token null or invalid: " + jwt);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract token from authorization-header if contained and valid.
     *
     * @param request Request-object
     * @return Token-Code as string. Null if not valid / contained
     */
    private String parseJwt(HttpServletRequest request) {

        final var headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {

            return headerAuth.substring(7);
        }

        return null;
    }
}