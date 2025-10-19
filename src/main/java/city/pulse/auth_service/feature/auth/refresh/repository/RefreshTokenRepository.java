package city.pulse.auth_service.feature.auth.refresh.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Query("""
           select r from RefreshToken r
           where r.tokenHash = :hash
             and r.revoked = false
             and r.expiresAt > :now
           """)
    Optional<RefreshToken> findActiveByHash(@Param("hash") String hash, @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RefreshToken r set r.revoked = true where r.id = :id")
    void revokeById(@Param("id") Long id);

    //@Modifying(clearAutomatically = true, flushAutomatically = true)
    //@Query("update RefreshToken r set r.revoked = true where r.user = :user and r.revoked = false")
    //int revokeAllByUser(@Param("user") User user);

    //@Modifying
    //@Query("delete from RefreshToken r where r.revoked = true or r.expiresAt <= :now")
    //int purgeObsolete(@Param("now") Instant now);
}
