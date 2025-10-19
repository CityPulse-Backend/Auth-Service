package city.pulse.auth_service.feature.auth.jwt.controller;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import city.pulse.auth_service.feature.auth.config.jwt.JwtConfig;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class JwkSetController {
    private final JwtConfig jwtConfig;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwkSet() {
        return new JWKSet(jwtConfig.getRsaKeyWithPublicKey()).toJSONObject();
    }
}