package proyecto.web.serviceguideBackend.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginResponse {

    String message;
    Boolean status;
}
