package proyecto.web.serviceguideBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptDto {

    private Long id;

    private Long waterPrice;
    private Long waterAmount;

    private Long seweragePrice;
    private Long sewerageAmount;

    private Long energyPrice;
    private Long energyAmount;

    private Long gasPrice;
    private Long gasAmount;

    private User user;
}
