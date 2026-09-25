package proyecto.web.serviceguideBackend.user.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String email;

    // Vacio significa "no cambiar la contrasena" (asi la manda el frontend);
    // si trae contenido, se exige el minimo de 8 caracteres.
    @Nullable
    @Pattern(regexp = "^$|.{8,100}", message = "Password must be empty or at least 8 characters")
    private String password;

}
