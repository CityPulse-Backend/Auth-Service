package city.pulse.auth_service.feature.auth.refresh.data;

import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.register.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class RefreshTokenTestData {
    public static final String VALID_TOKEN_HASH = "a-valid-and-correct-token-hash-for-testing";

    public static RefreshToken.RefreshTokenBuilder aValidToken(User user) {
        return RefreshToken.builder()
                .user(user)
                .tokenHash(VALID_TOKEN_HASH)
                .revoked(false)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .issuedAt(Instant.now())
                .ip("127.0.0.1")
                .userAgent("test-agent");
    }
}