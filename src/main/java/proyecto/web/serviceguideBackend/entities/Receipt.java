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

    @OneToOne
    @JoinColumn(name = "water_id")
    private Water water;

    @OneToOne
    @JoinColumn(name = "sewerage_id")
    private Sewerage sewerage;

    @OneToOne
    @JoinColumn(name = "energy_id")
    private Energy energy;

    @OneToOne
    @JoinColumn(name = "gas_id")
    private Gas gas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

}
