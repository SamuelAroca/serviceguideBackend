package proyecto.web.serviceguideBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicServicesReceipt {

    @Id
    @Column(length = 255)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@NotBlank
    private Double waterPrice;

    //@NotBlank
    private Double sewerPrice;

    //@NotBlank
    private Double electricityPrice;

    //@NotBlank
    private Double gasPrice;
}
