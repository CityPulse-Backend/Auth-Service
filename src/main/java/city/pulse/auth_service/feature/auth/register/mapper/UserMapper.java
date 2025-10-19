package city.pulse.auth_service.feature.auth.register.mapper;

import org.springframework.stereotype.Component;
import city.pulse.auth_service.feature.auth.register.dto.UserResponseDTO;
import city.pulse.auth_service.feature.auth.register.model.User;

@Component
public class UserMapper {
    public UserResponseDTO toDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}