package city.pulse.auth_service.feature.auth.common.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("A user with the given username or email already exists");
    }
}
