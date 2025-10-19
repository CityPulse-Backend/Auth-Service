package city.pulse.auth_service.feature.auth.register.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 32, message = "Username must be between 4 and 32 characters long")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 32, message = "Password must be between 6 and 32 characters long")
    private String password;

    @Email(message = "Email does not exist")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 32, message = "Email must be less then 32 characters long")
    private String email;
}