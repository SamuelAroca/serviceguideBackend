package proyecto.web.serviceguideBackend.emailpassword.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailValuesDto {

    private String mailFrom;
    private String mailTo;
    private String subject;
    private String userName;
    private String token;
}
