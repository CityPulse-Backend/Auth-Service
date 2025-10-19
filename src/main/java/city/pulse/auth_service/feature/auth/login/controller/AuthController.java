package city.pulse.auth_service.feature.auth.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import city.pulse.auth_service.feature.auth.login.dto.LoginRequestDTO;
import city.pulse.auth_service.feature.auth.login.dto.LoginResponseDTO;
import city.pulse.auth_service.feature.auth.login.dto.RefreshTokenDTO;
import city.pulse.auth_service.feature.auth.login.service.LoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return loginService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        return loginService.refresh(refreshTokenDTO.getToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        loginService.logout(refreshTokenDTO.getToken());
        return ResponseEntity.noContent().build();
    }
}
