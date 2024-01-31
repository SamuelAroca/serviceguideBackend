package proyecto.web.serviceguideBackend.user.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponse {

    private String message;
    private HttpStatus status;
    private String token;

}
