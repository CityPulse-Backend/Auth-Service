package city.pulse.auth_service.feature.auth.register.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import city.pulse.auth_service.feature.auth.register.dto.UserResponseDTO;
import city.pulse.auth_service.feature.auth.common.exception.UserAlreadyExistsException;
import city.pulse.auth_service.feature.auth.register.mapper.UserMapper;
import city.pulse.auth_service.feature.auth.register.model.Role;
import city.pulse.auth_service.feature.auth.register.model.User;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;
import city.pulse.auth_service.feature.auth.register.service.RegisterService;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserResponseDTO createUser(String username, String password, String email) {
        checkIfUserExists(username, email);

        var user = User.builder()
                .username(username)
                .password(encoder.encode(password))
                .email(email)
                .role(Role.USER)
                .build();

        try {
            var savedUser = repository.save(user);
            return mapper.toDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException();
        }
    }

    private void checkIfUserExists(String username, String email) {
        if(repository.existsByUsername(username) || repository.existsByEmail(email)) {
            throw new UserAlreadyExistsException();
        }
    }
}
