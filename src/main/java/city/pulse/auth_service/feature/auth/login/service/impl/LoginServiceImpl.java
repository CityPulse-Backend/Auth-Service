package city.pulse.auth_service.feature.auth.login.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import city.pulse.auth_service.feature.auth.jwt.service.JwtService;
import city.pulse.auth_service.feature.auth.login.dto.LoginResponseDTO;
import city.pulse.auth_service.feature.auth.login.service.LoginService;
import city.pulse.auth_service.feature.auth.refresh.service.RefreshTokenService;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository repository;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = repository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        var access = jwtService.createAccessToken(user.getId(), user.getRole());
        var refresh = refreshTokenService.issue(user, currentUA(), currentIP());

        return new LoginResponseDTO(access, refresh);
    }

    @Override
    public LoginResponseDTO refresh(String rawToken) {
        var rt = refreshTokenService.validateActive(rawToken);
        var user = rt.getUser();
        var newAccess = jwtService.createAccessToken(user.getId(), user.getRole());
        var newRefresh = refreshTokenService.rotate(rt, currentUA(), currentIP());
        return new LoginResponseDTO(newAccess, newRefresh);
    }

    @Override
    public void logout(String refreshToken) {
        var rt = refreshTokenService.validateActive(refreshToken);
        refreshTokenService.revokeById(rt.getId());
    }

    private String currentUA() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(a -> a.getRequest().getHeader("User-Agent")).orElse("unknown");
    }

    private String currentIP() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(a -> a.getRequest().getRemoteAddr()).orElse("unknown");
    }
}
