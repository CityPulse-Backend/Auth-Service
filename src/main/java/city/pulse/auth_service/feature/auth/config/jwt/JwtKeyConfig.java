package city.pulse.auth_service.feature.auth.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jwt.keys")
public class JwtKeyConfig {
    private String privateKeyPath;
    private String publicKeyPath;

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        return (RSAPrivateKey) readKey(privateKeyPath, true);
    }

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        return (RSAPublicKey) readKey(publicKeyPath, false);
    }

    private Object readKey(String path, boolean isPrivate) throws Exception {
        try (var inputStream = getClass().getResourceAsStream(path.replace("classpath:", "/"))) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Key file not found: " + path);
            }

            var key = new String(inputStream.readAllBytes())
                    .replaceAll("-----\\w+ (PRIVATE|PUBLIC) KEY-----", "")
                    .replaceAll("\\s", "");

            var decoded = Base64.getDecoder().decode(key);
            var kf = KeyFactory.getInstance("RSA");

            return isPrivate
                    ? kf.generatePrivate(new PKCS8EncodedKeySpec(decoded))
                    : kf.generatePublic(new X509EncodedKeySpec(decoded));
        }
    }
}
