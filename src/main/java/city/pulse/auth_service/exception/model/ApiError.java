package city.pulse.auth_service.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(Instant timestamp, int status, String message, String path, List<ErrorDetails> errors) {
    public record ErrorDetails(String field, String message) {}

    public ApiError(int status, String message, String path, List<ErrorDetails> errors) {
        this(Instant.now(), status, message, path, errors);
    }

    public ApiError(int status, String message, String path) {
        this(status, message, path, null);
    }

    public static ResponseEntity<ApiError> buildResponse(String message, HttpStatus status, HttpServletRequest request, List<ErrorDetails> errors) {
        var body = new ApiError(status.value(), message, request.getRequestURI(), errors);
        return new ResponseEntity<>(body, status);
    }

    public static ResponseEntity<ApiError> buildResponse(String message, HttpStatus status, HttpServletRequest request) {
        return buildResponse(message, status, request, null);
    }
}
