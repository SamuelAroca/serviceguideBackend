package proyecto.web.serviceguideBackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private String name;

    @NotNull
    private Integer stratum;

    @NotNull
    private String neighborhood;

    @Nullable
    private String address;

    @NotNull
    private String contract;

    @OneToOne
    @JoinColumn(name = "FK_COLOMBIAN_CITY", updatable = false, nullable = false)
    private ColombianCities cities;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<ServiceReceipt> serviceReceipts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_USER")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private User user;

    public void setServiceReceipt(List<ServiceReceipt> serviceReceipts) {
        this.serviceReceipts = serviceReceipts;
        for (ServiceReceipt serviceReceipt : serviceReceipts) {
            serviceReceipt.setHouse(this);
        }
    }
}
