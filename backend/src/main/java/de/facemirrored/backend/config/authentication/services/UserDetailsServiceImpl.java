package de.facemirrored.backend.config.authentication.services;

import de.facemirrored.backend.config.authentication.springboot.UserDetailsImpl;
import de.facemirrored.backend.database.repository.UserRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User-Service-Implementierung, welche sich um das Laden der User-Daten kümmert. Diese werden vom
 * dazugehörigen {@link UserRepository} abgerufen. Klassisch wird eine Implementierung der
 * {@link UserDetails UserDetails-Klasse} zurückgegeben. Hierfür kümmert sich die
 * {@link UserDetailsImpl UserDetailsImpl-Klasse}.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  UserRepository userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }
}