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
public class EnergyDto {

    private Long id;

    @NotNull
    private String receiptName;

    @NotNull
    private Long price;

    @NotNull
    private Long amount;

    @NotNull
    private Date date;

    @NotNull
    private User user;

}
