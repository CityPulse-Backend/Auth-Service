package city.pulse.auth_service.feature.auth.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {
    @NotBlank(message = "Token cannot be blank")
    @Size(min = 43, max = 44, message = "Token must be between 43 and 44 characters long")
    private String token;
}
