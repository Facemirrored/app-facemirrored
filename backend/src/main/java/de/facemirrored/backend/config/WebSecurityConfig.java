package de.facemirrored.backend.config;

import de.facemirrored.backend.config.authentication.jwtauth.AuthEntryPointJwt;
import de.facemirrored.backend.config.authentication.jwtauth.AuthTokenFilter;
import de.facemirrored.backend.config.authentication.services.UserDetailsServiceImpl;
import de.facemirrored.backend.database.model.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  final UserDetailsServiceImpl userDetailsService;

  private final AuthEntryPointJwt unauthorizedHandler;


  @Autowired
  public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
      AuthEntryPointJwt authEntryPointJwt) {
    this.userDetailsService = userDetailsService;
    this.unauthorizedHandler = authEntryPointJwt;
  }

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        // wir nutzen ein von Spring Boot verwaltetes Token-Repository für CSRF Security
        // TODO: 'withHttpOnlyFalse' könnte man weg lassen für mehr Sicherheit,
        // TODO: da wir wahrscheinlich nicht im FE drauf zugreifen müssen und somit Axios das automatisiert übernhemen lassen
        .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
        // wir nutzen einen eigenen ExceptionHandler für unauthorisierte Zugriffe
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
        .and()
        // unter der Haube sind wir technisch gesehen stateless unterwegs und verwalten den State selber via JWT-Token
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        // default CORS config mit erlaubten localhost-Zugriff
        .cors().configurationSource(httpServletRequest -> {
          var config = new CorsConfiguration();
          config.addAllowedOrigin("http://localhost:8081");
          return config;
        })
        .and()
        // admin API-Zugriff
        .authorizeRequests().antMatchers("/api/admin").hasRole(ERole.ADMIN.name())
        .and()
        // public API-Zugriff
        .authorizeRequests().antMatchers("/api/public").permitAll();

    //
    http.addFilterBefore(
        authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);
  }
}
