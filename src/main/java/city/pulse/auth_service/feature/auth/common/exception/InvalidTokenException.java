package city.pulse.auth_service.feature.auth.common.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("Invalid refresh token");
    }
}
