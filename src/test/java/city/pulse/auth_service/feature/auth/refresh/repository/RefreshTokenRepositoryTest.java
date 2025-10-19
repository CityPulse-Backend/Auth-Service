package city.pulse.auth_service.feature.auth.refresh.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import city.pulse.auth_service.feature.auth.common.data.UserTestData;
import city.pulse.auth_service.feature.auth.refresh.data.RefreshTokenTestData;
import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.register.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        this.savedUser = entityManager.persist(UserTestData.aliceBeforeSaving());
    }

    @Test
    void shouldFindActiveTokenByHash() {
        var token = RefreshTokenTestData.aValidToken(savedUser).build();
        entityManager.persist(token);

        var foundToken = refreshTokenRepository.findActiveByHash(RefreshTokenTestData.VALID_TOKEN_HASH, Instant.now());

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getTokenHash()).isEqualTo(RefreshTokenTestData.VALID_TOKEN_HASH);
    }

    @Test
    void shouldNotFindTokenIfItIsRevoked() {
        var token = RefreshTokenTestData.aValidToken(savedUser)
                .tokenHash("revoked-hash")
                .revoked(true)
                .build();
        entityManager.persist(token);

        var foundToken = refreshTokenRepository.findActiveByHash("revoked-hash", Instant.now());

        assertThat(foundToken).isNotPresent();
    }

    @Test
    void shouldNotFindTokenIfItIsExpired() {
        var token = RefreshTokenTestData.aValidToken(savedUser)
                .tokenHash("expired-hash")
                .expiresAt(Instant.now().minus(1, ChronoUnit.SECONDS)) // <-- Кастомізація
                .build();
        entityManager.persist(token);

        var foundToken = refreshTokenRepository.findActiveByHash("expired-hash", Instant.now());

        assertThat(foundToken).isNotPresent();
    }

    @Test
    void shouldNotFindTokenIfHashIsWrong() {
        var token = RefreshTokenTestData.aValidToken(savedUser).build();
        entityManager.persist(token);

        var foundToken = refreshTokenRepository.findActiveByHash("wrong-hash", Instant.now());

        assertThat(foundToken).isNotPresent();
    }

    @Test
    void shouldRevokeTokenById() {
        var token = RefreshTokenTestData.aValidToken(savedUser).build();
        var savedToken = entityManager.persistFlushFind(token);
        assertThat(savedToken.isRevoked()).isFalse();

        refreshTokenRepository.revokeById(savedToken.getId());

        var updatedToken = entityManager.find(RefreshToken.class, savedToken.getId());
        assertThat(updatedToken).isNotNull();
        assertThat(updatedToken.isRevoked()).isTrue();
    }
}