package proyecto.web.serviceguideBackend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PublicServicesReceiptDTO {

    private Long id;
    private Double waterPrice;
    private Double sewerPrice;
    private Double electricityPrice;
    private Double gasPrice;
}
