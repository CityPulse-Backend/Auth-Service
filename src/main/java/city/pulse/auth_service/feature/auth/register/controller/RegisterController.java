package city.pulse.auth_service.feature.auth.register.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import city.pulse.auth_service.feature.auth.register.dto.UserCreateDTO;
import city.pulse.auth_service.feature.auth.register.dto.UserResponseDTO;
import city.pulse.auth_service.feature.auth.register.service.RegisterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/${app.version}/auth")
public class RegisterController {
    private final RegisterService registerService;

    @Value("${app.version}")
    private String appVersion;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        var created = registerService.createUser(userCreateDTO.getUsername(), userCreateDTO.getPassword(), userCreateDTO.getEmail());
        var location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/" + appVersion + "/users/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }
}
