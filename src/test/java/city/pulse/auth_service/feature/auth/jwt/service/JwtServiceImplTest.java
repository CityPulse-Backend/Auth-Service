package city.pulse.auth_service.feature.auth.jwt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import city.pulse.auth_service.feature.auth.config.jwt.JwtConfig;
import city.pulse.auth_service.feature.auth.jwt.service.impl.JwtServiceImpl;
import city.pulse.auth_service.feature.auth.register.model.Role;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    @Mock
    private JwtEncoder encoder;

    @Mock
    private JwtConfig cfg;

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    void shouldCreateAccessTokenWithCorrectClaims() {
        final Long userId = 123L;
        final Role userRole = Role.USER;
        final String issuer = "https://my-test-app.com";
        final long ttlMs = 3600000L;
        final String expectedTokenValue = "generated.test.token";

        when(cfg.getIss()).thenReturn(issuer);
        when(cfg.getAccessTokenTtlMs()).thenReturn(ttlMs);

        var captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        var mockedJwt = mock(Jwt.class);

        when(mockedJwt.getTokenValue()).thenReturn(expectedTokenValue);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockedJwt);

        var actualToken = jwtService.createAccessToken(userId, userRole);

        assertThat(actualToken).isEqualTo(expectedTokenValue);

        verify(encoder).encode(captor.capture());

        var capturedClaims = captor.getValue().getClaims();

        assertThat(capturedClaims.getSubject()).isEqualTo(userId.toString());
        assertThat(capturedClaims.getIssuer().toString()).isEqualTo(issuer);
        assertThat(capturedClaims.getClaim("role").toString()).isEqualTo(userRole.name());

        var issuedAt = capturedClaims.getIssuedAt();
        var expiresAt = capturedClaims.getExpiresAt();

        assertThat(ChronoUnit.MILLIS.between(issuedAt, expiresAt)).isEqualTo(ttlMs);
    }
}