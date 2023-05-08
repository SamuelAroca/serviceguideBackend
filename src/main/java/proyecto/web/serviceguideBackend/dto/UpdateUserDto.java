package proyecto.web.serviceguideBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserDto {

    private String message;
    private HttpStatus status;
    private String token;

}
