package proyecto.web.serviceguideBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class Message {

    private String message;
    private HttpStatus status;

    public Message(String message) {
        this.message = message;
    }
}
