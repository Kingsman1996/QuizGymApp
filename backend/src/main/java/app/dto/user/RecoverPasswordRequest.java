package app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecoverPasswordRequest {
    @NotBlank(message = "{password.required}")
    @Size(min = 6, message = "{password.length}")
    private String confirmPassword;

    @NotBlank(message = "{password.required}")
    @Size(min = 6, message = "{password.length}")
    private String password;

    @NotBlank(message = "{token.required}")
    private String code;

    @NotBlank(message = "{email.required}")
    private String email;
}
