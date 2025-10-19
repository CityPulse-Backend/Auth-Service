package city.pulse.auth_service.feature.auth.jwt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import city.pulse.auth_service.feature.auth.config.jwt.JwtConfig;
import city.pulse.auth_service.feature.auth.jwt.service.JwtService;
import city.pulse.auth_service.feature.auth.register.model.Role;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder encoder;
    private final JwtConfig cfg;

    @Override
    public String createAccessToken(Long userId, Role role) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(cfg.getIss())
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusMillis(cfg.getAccessTokenTtlMs()))
                .claim("role", role.name())
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
