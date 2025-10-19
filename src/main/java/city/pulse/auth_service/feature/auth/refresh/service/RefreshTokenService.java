package city.pulse.auth_service.feature.auth.refresh.service;

import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.register.model.User;

public interface RefreshTokenService {
    String issue(User user, String userAgent, String ip);
    RefreshToken validateActive(String rawToken);
    String rotate(RefreshToken oldToken, String ua, String ip);
    void revokeById(Long id);
    //void revokeAll(User user);
}
