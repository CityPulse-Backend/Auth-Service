package city.pulse.auth_service.feature.auth.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import city.pulse.auth_service.exception.security.RestAccessDeniedHandler;
import city.pulse.auth_service.exception.security.RestAuthenticationEntryPoint;
import city.pulse.auth_service.feature.auth.config.security.SecurityConfig;
import city.pulse.auth_service.feature.auth.login.dto.LoginRequestDTO;
import city.pulse.auth_service.feature.auth.login.dto.LoginResponseDTO;
import city.pulse.auth_service.feature.auth.login.dto.RefreshTokenDTO;
import city.pulse.auth_service.feature.auth.login.service.LoginService;
import city.pulse.auth_service.feature.auth.register.repository.UserRepository;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @MockitoBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockitoBean
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Value("${app.version}")
    private String apiVersion;

    @Test
    void shouldLoginSuccessfullyAndReturnTokens() throws Exception {
        var loginRequest = new LoginRequestDTO("user", "password");
        var loginResponse = new LoginResponseDTO("access-token", "refresh-token");

        when(loginService.login("user", "password")).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v" + apiVersion + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(loginService).login("user", "password");
    }

    @Test
    void shouldRefreshTokensSuccessfully() throws Exception {
        var validToken = "K8v5tE5wX2bH1rQ9vY6nF3cM7gA4jL0pS1dR8uI5oZ3e";
        var refreshTokenRequest = new RefreshTokenDTO(validToken);
        var loginResponse = new LoginResponseDTO("new-access-token", "new-refresh-token");

        when(loginService.refresh(validToken)).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v" + apiVersion + "/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(loginService).refresh(validToken);
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        var validToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ1234567890aBcDeFgH";
        var refreshTokenRequest = new RefreshTokenDTO(validToken);

        doNothing().when(loginService).logout(validToken);

        mockMvc.perform(post("/api/v" + apiVersion + "/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isNoContent());

        verify(loginService).logout(validToken);
    }

    @Test
    void shouldReturnBadRequestWhenLoginBodyIsInvalid() throws Exception {
        var invalidLoginRequest = new LoginRequestDTO("", "password");

        mockMvc.perform(post("/api/v" + apiVersion + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }
}
