package proyecto.web.serviceguideBackend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginDTO {

    private String email;
    private String password;
}

