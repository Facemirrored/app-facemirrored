package de.facemirrored.backend.database.repository;

import de.facemirrored.backend.database.model.ERole;
import de.facemirrored.backend.database.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(final ERole name);
}
