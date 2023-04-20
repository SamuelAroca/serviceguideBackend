package proyecto.web.serviceguideBackend.dto;

import jakarta.validation.constraints.NotNull;
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
public class GasDto {

    private Long id;

    @NotNull
    private String receiptName;

    @NotNull
    private Double price;

    @NotNull
    private Double amount;

    @NotNull
    private Date date;

    @NotNull
    private User user;

}
