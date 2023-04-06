package proyecto.web.serviceguideBackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "receipts")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receipt")
    private Long id;

    @Column(name = "water_price")
    private Long waterPrice;
    @Column(name = "water_amount")
    private Long waterAmount;

    @Column(name = "sewerage_price")
    private Long seweragePrice;
    @Column(name = "sewerage_amount")
    private Long sewerageAmount;

    @Column(name = "energy_price")
    private Long energyPrice;
    @Column(name = "energy_amount")
    private Long energyAmount;

    @Column(name = "gas_price")
    private Long gasPrice;
    @Column(name = "gas_amount")
    private Long gasAmount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

}
