package proyecto.web.serviceguideBackend.house;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.user.User;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "house", uniqueConstraints = {@UniqueConstraint(columnNames = {"contract"})})
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100)
    private String name;

    @NotNull
    @Column(length = 3)
    private Integer stratum;

    @NotNull
    @Column(length = 100)
    private String neighborhood;

    @Nullable
    @Column(length = 100)
    private String address;

    @NotNull
    @Column(length = 100)
    private String contract;

    // Antes era @OneToOne: JPA pone un constraint UNIQUE sobre fk_city, o
    // sea que en todo el sistema (no por usuario) solo podia existir UNA
    // casa por ciudad. La segunda casa que cualquier usuario intentara
    // registrar en una ciudad ya usada tiraba 500
    // (DataIntegrityViolationException por el unique constraint).
    @ManyToOne
    @JoinColumn(name = "FK_CITY", nullable = false)
    private City cities;

    // WRITE_ONLY: sin esto, cada vez que se lista una casa Jackson serializa
    // TODA su coleccion de recibos (lazy -> dispara una query por casa, y en
    // un usuario real ya son 800+ recibos en total). El detalle de una casa
    // trae sus recibos por separado via /api/receipt/getReceiptsByHouseAndUser.
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Receipt> receipts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_USER")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private User user;

}
