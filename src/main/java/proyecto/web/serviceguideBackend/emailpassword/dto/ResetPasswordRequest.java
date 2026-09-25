package proyecto.web.serviceguideBackend.emailpassword.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String code;
    @NotBlank
    @Size(min = 8, max = 100)
    private String newPassword;
}
