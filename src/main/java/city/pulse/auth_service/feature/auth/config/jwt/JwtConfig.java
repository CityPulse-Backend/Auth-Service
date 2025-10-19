package city.pulse.auth_service.feature.auth.config.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Configuration
@RequiredArgsConstructor
public class JwtConfig {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    @Value("${app.jwt.access-token-ttl-ms}")
    private Long accessTokenTtlMs;

    @Value("${app.jwt.refresh-token-ttl-ms}")
    private Long refreshTokenTtlMs;

    @Value("${app.jwt.iss}")
    private String iss;

    @Value("${app.jwt.key-id}")
    private String keyId;

    public RSAKey getRsaKeyWithPrivateKey() {
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();
    }

    public RSAKey getRsaKeyWithPublicKey() {
        return new RSAKey.Builder(publicKey)
                .keyID(keyId)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(getRsaKeyWithPrivateKey())));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var dec = NimbusJwtDecoder.withPublicKey(publicKey).build();
        dec.setJwtValidator(JwtValidators.createDefaultWithIssuer(iss));
        return dec;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        var g = new JwtGrantedAuthoritiesConverter();
        g.setAuthoritiesClaimName("role");
        g.setAuthorityPrefix("ROLE_");
        var c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(g);
        return c;
    }
}