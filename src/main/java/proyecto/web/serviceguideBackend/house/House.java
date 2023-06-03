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
@Table(name = "house")
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

    @OneToOne
    @JoinColumn(name = "FK_CITY", nullable = false)
    private City cities;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<Receipt> receipts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_USER")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private User user;

    public void setServiceReceipt(List<Receipt> receipts) {
        this.receipts = receipts;
        for (Receipt receipt : receipts) {
            receipt.setHouse(this);
        }
    }
}
