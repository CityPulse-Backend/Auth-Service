package city.pulse.auth_service.feature.auth.refresh.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import city.pulse.auth_service.feature.auth.common.exception.InvalidTokenException;
import city.pulse.auth_service.feature.auth.common.util.Tokens;
import city.pulse.auth_service.feature.auth.config.jwt.JwtConfig;
import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.refresh.repository.RefreshTokenRepository;
import city.pulse.auth_service.feature.auth.refresh.service.impl.RefreshTokenServiceImpl;
import city.pulse.auth_service.feature.auth.register.model.User;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository repository;

    @Mock
    private JwtConfig cfg;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void shouldIssueNewTokenAndSaveItsHash() {
        var user = new User();
        var ua = "test-agent";
        var ip = "127.0.0.1";
        var rawToken = "raw-random-token-string-12345";
        var hashedToken = "hashed-version-of-the-token";
        var ttlMs = 86400000L;

        when(cfg.getRefreshTokenTtlMs()).thenReturn(ttlMs);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        try (var mockedTokens = Mockito.mockStatic(Tokens.class)) {
            mockedTokens.when(() -> Tokens.randomToken(32)).thenReturn(rawToken);
            mockedTokens.when(() -> Tokens.sha256(rawToken)).thenReturn(hashedToken);

            var resultToken = refreshTokenService.issue(user, ua, ip);

            assertThat(resultToken).isEqualTo(rawToken);

            verify(repository).save(captor.capture());
            RefreshToken savedToken = captor.getValue();

            assertThat(savedToken.getUser()).isEqualTo(user);
            assertThat(savedToken.getTokenHash()).isEqualTo(hashedToken);
            assertThat(savedToken.getUserAgent()).isEqualTo(ua);
            assertThat(savedToken.getIp()).isEqualTo(ip);
            assertThat(savedToken.isRevoked()).isFalse();
        }
    }

    @Test
    void shouldValidateActiveTokenSuccessfully() {
        var rawToken = "valid-raw-token";
        var hashedToken = "valid-hashed-token";
        var expectedToken = new RefreshToken();

        try (var mockedTokens = Mockito.mockStatic(Tokens.class)) {
            mockedTokens.when(() -> Tokens.sha256(rawToken)).thenReturn(hashedToken);
            when(repository.findActiveByHash(eq(hashedToken), any(Instant.class)))
                    .thenReturn(Optional.of(expectedToken));

            var actualToken = refreshTokenService.validateActive(rawToken);

            assertThat(actualToken).isEqualTo(expectedToken);
            verify(repository).findActiveByHash(eq(hashedToken), any(Instant.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenValidatingInactiveToken() {
        var rawToken = "invalid-raw-token";
        var hashedToken = "invalid-hashed-token";

        try (var mockedTokens = Mockito.mockStatic(Tokens.class)) {
            mockedTokens.when(() -> Tokens.sha256(rawToken)).thenReturn(hashedToken);
            when(repository.findActiveByHash(eq(hashedToken), any(Instant.class)))
                    .thenReturn(Optional.empty());

            assertThrows(InvalidTokenException.class, () -> {
                refreshTokenService.validateActive(rawToken);
            });
        }
    }

    @Test
    void shouldRotateToken() {
        var user = new User();
        user.setId(1L);
        var oldToken = RefreshToken.builder().user(user).revoked(false).build();
        var newRawToken = "new-raw-token-after-rotation";
        var newHashedToken = "new-hashed-token";

        var captor = ArgumentCaptor.forClass(RefreshToken.class);

        try (var mockedTokens = Mockito.mockStatic(Tokens.class)) {
            mockedTokens.when(() -> Tokens.randomToken(32)).thenReturn(newRawToken);
            mockedTokens.when(() -> Tokens.sha256(newRawToken)).thenReturn(newHashedToken);

            var resultToken = refreshTokenService.rotate(oldToken, "new-ua", "192.168.1.1");

            assertThat(resultToken).isEqualTo(newRawToken);

            verify(repository, times(2)).save(captor.capture());
            var savedTokens = captor.getAllValues();

            assertThat(savedTokens.get(0)).isEqualTo(oldToken);
            assertThat(savedTokens.get(0).isRevoked()).isTrue();

            assertThat(savedTokens.get(1).getTokenHash()).isEqualTo(newHashedToken);
            assertThat(savedTokens.get(1).getUser()).isEqualTo(user);
        }
    }

    @Test
    void shouldCallRepositoryToRevokeById() {
        var tokenId = 123L;

        refreshTokenService.revokeById(tokenId);

        verify(repository).revokeById(tokenId);
    }
}