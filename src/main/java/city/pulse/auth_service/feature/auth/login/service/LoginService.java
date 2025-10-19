package city.pulse.auth_service.feature.auth.login.service;

import city.pulse.auth_service.feature.auth.login.dto.LoginResponseDTO;

public interface LoginService {
    LoginResponseDTO login(String username, String password);
    LoginResponseDTO refresh(String token);
    void logout(String refreshToken);
}
