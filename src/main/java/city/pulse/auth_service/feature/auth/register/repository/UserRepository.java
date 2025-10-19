package city.pulse.auth_service.feature.auth.register.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import city.pulse.auth_service.feature.auth.register.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
