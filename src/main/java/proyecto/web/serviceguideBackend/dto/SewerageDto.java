package proyecto.web.serviceguideBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SewerageDto {

    private Long id;

    private Long price;

    private Long amount;

    private Date date;

    private User user;

}
