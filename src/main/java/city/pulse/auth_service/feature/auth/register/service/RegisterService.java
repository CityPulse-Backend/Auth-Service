package city.pulse.auth_service.feature.auth.register.service;

import city.pulse.auth_service.feature.auth.register.dto.UserResponseDTO;

public interface RegisterService {
    UserResponseDTO createUser(String username, String password, String email);
}
