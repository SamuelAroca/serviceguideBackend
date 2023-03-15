package proyecto.web.serviceguideBackend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
}
