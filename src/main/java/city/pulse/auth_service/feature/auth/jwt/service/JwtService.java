package city.pulse.auth_service.feature.auth.jwt.service;

import city.pulse.auth_service.feature.auth.register.model.Role;

public interface JwtService {
    String createAccessToken(Long userId, Role role);
}
