package city.pulse.auth_service.feature.auth.common.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokensTest {
    @Test
    void shouldGenerateUrlSafeTokenOfCorrectLength() {
        var inputBytes = 32;
        var expectedLength = 43;

        var token = Tokens.randomToken(inputBytes);

        assertThat(token).isNotNull();
        assertThat(token.length()).isEqualTo(expectedLength);
        assertThat(token).matches("^[A-Za-z0-9_-]+$");
    }

    @Test
    void shouldGenerateDifferentTokensOnSubsequentCalls() {
        var token1 = Tokens.randomToken(16);
        var token2 = Tokens.randomToken(16);

        assertThat(token1).isNotNull();
        assertThat(token2).isNotNull();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldReturnEmptyStringForZeroBytes() {
        var token = Tokens.randomToken(0);
        assertThat(token).isEmpty();
    }

    @Test
    void shouldThrowExceptionForNegativeBytes() {
        assertThrows(NegativeArraySizeException.class, () -> {
            Tokens.randomToken(-1);
        });
    }

    @Test
    void shouldProduceCorrectAndConsistentSha256Hash() {
        var input = "hello world";
        var expectedHash = "uU0nuZNNPgilLlLX2n2r+sSE7+N6U4DukIj3rOLvzek=";

        var actualHash = Tokens.sha256(input);

        assertThat(actualHash).isEqualTo(expectedHash);
    }

    @Test
    void shouldProduceCorrectHashForEmptyString() {
        var input = "";
        var expectedHash = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";

        var actualHash = Tokens.sha256(input);

        assertThat(actualHash).isEqualTo(expectedHash);
    }

    @Test
    void shouldThrowExceptionForNullInput() {
        assertThrows(NullPointerException.class, () -> {
            Tokens.sha256(null);
        });
    }
}