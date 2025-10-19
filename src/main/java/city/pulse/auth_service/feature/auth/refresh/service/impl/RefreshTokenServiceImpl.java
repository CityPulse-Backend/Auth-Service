package city.pulse.auth_service.feature.auth.refresh.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import city.pulse.auth_service.feature.auth.config.jwt.JwtConfig;
import city.pulse.auth_service.feature.auth.common.exception.InvalidTokenException;
import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.refresh.repository.RefreshTokenRepository;
import city.pulse.auth_service.feature.auth.common.util.Tokens;
import city.pulse.auth_service.feature.auth.refresh.service.RefreshTokenService;
import city.pulse.auth_service.feature.auth.register.model.User;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final JwtConfig cfg;

    @Override
    public String issue(User user, String ua, String ip) {
        var raw = Tokens.randomToken(32);
        var hash = Tokens.sha256(raw);
        repository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hash)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(cfg.getRefreshTokenTtlMs()))
                .revoked(false)
                .userAgent(ua)
                .ip(ip)
                .build());
        return raw;
    }

    @Override
    public RefreshToken validateActive(String raw) {
        return repository.findActiveByHash(Tokens.sha256(raw), Instant.now())
                .orElseThrow(InvalidTokenException::new);
    }

    @Override
    public String rotate(RefreshToken oldToken, String ua, String ip) {
        oldToken.setRevoked(true);
        repository.save(oldToken);
        return issue(oldToken.getUser(), ua, ip);
    }

    @Override
    public void revokeById(Long id) {
        repository.revokeById(id);
    }

    //@Override
    //public void revokeAll(User user) {
    //    repository.revokeAllByUser(user);
    //}
}
