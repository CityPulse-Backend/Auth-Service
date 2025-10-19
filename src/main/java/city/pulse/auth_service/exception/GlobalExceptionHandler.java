package city.pulse.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import city.pulse.auth_service.exception.model.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var errors = ex.getBindingResult().getFieldErrors().stream().map(fe ->
                new ApiError.ErrorDetails(fe.getField(), fe.getDefaultMessage())
        ).toList();
        return ApiError.buildResponse("The submitted data is invalid. Please check the marked fields", HttpStatus.BAD_REQUEST, req, errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCred(HttpServletRequest req) {
        return ApiError.buildResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, req);
    }
}
