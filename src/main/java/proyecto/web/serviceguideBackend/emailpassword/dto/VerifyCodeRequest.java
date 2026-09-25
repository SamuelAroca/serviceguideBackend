package proyecto.web.serviceguideBackend.emailpassword.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String code;
}
