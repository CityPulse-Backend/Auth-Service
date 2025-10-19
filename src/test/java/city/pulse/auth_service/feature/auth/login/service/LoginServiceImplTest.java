package city.pulse.auth_service.feature.auth.login.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import city.pulse.auth_service.feature.auth.jwt.service.JwtService;
import city.pulse.auth_service.feature.auth.login.dto.LoginResponseDTO;
import city.pulse.auth_service.feature.auth.login.service.impl.LoginServiceImpl;
import city.pulse.auth_service.feature.auth.refresh.model.RefreshToken;
import city.pulse.auth_service.feature.auth.refresh.service.RefreshTokenService;
import city.pulse.auth_service.feature.auth.common.data.UserTestData;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserRepository repository;
    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        var attributes = new ServletRequestAttributes(httpServletRequest);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    void shouldLoginSuccessfullyAndReturnTokens() {
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Test-User-Agent");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var username = UserTestData.ALICE;
        var password = "password";
        var accessToken = "new.access.token";
        var refreshToken = "new.refresh.token";
        var user = UserTestData.aliceSaved();

        when(repository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.createAccessToken(user.getId(), user.getRole())).thenReturn(accessToken);
        when(refreshTokenService.issue(eq(user), anyString(), anyString())).thenReturn(refreshToken);

        var response = loginService.login(username, password);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(username, password));
        verify(repository).findByUsername(username);
        verify(jwtService).createAccessToken(user.getId(), user.getRole());
        verify(refreshTokenService).issue(user, "Test-User-Agent", "127.0.0.1");
    }

    @Test
    void shouldThrowBadCredentialsWhenUserNotFound() {
        var username = "nonexistent";
        var password = "password";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> {
            loginService.login(username, password);
        });

        verify(jwtService, never()).createAccessToken(any(), any());
        verify(refreshTokenService, never()).issue(any(), any(), any());
    }

    @Test
    void shouldRefreshTokensSuccessfully() {
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Test-User-Agent");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var rawToken = "old.refresh.token";
        var newAccessToken = "refreshed.access.token";
        var newRefreshToken = "rotated.refresh.token";
        var user = UserTestData.aliceSaved();
        var rt = new RefreshToken();
        rt.setUser(user);

        when(refreshTokenService.validateActive(rawToken)).thenReturn(rt);
        when(jwtService.createAccessToken(user.getId(), user.getRole())).thenReturn(newAccessToken);
        when(refreshTokenService.rotate(eq(rt), anyString(), anyString())).thenReturn(newRefreshToken);

        LoginResponseDTO response = loginService.refresh(rawToken);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);

        verify(refreshTokenService).validateActive(rawToken);
        verify(jwtService).createAccessToken(user.getId(), user.getRole());
        verify(refreshTokenService).rotate(rt, "Test-User-Agent", "127.0.0.1");
    }

    @Test
    void shouldLogoutSuccessfully() {
        var refreshToken = "token.to.revoke";
        var rt = new RefreshToken();
        rt.setId(123L);

        when(refreshTokenService.validateActive(refreshToken)).thenReturn(rt);
        doNothing().when(refreshTokenService).revokeById(rt.getId());

        loginService.logout(refreshToken);

        verify(refreshTokenService).validateActive(refreshToken);
        verify(refreshTokenService).revokeById(rt.getId());
    }
}