package proyecto.web.serviceguideBackend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class UpdateResponse {

    private String message;
    private HttpStatus status;
    private String token;

}
