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
    private String city;

    @NotNull
    private String neighborhood;

    @Nullable
    private String address;

    @NotNull
    private String contract;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<WaterReceipt> waterReceipts = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<SewerageReceipt> sewerageReceipt = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<EnergyReceipt> energyReceipts = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<GasReceipt> gasReceipts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private User user;

    public void setWaterReceipts(List<WaterReceipt> waterReceipts) {
        this.waterReceipts = waterReceipts;
        for (WaterReceipt waterReceipt : waterReceipts) {
            waterReceipt.setHouse(this);
        }
    }

    public void setSewerageReceipt(List<SewerageReceipt> sewerageReceipt) {
        this.sewerageReceipt = sewerageReceipt;
        for (SewerageReceipt sewerageReceipt1 : sewerageReceipt) {
            sewerageReceipt1.setHouse(this);
        }
    }

    public void setEnergyReceipts(List<EnergyReceipt> energyReceipts) {
        this.energyReceipts = energyReceipts;
        for (EnergyReceipt energyReceipt1 : energyReceipts) {
            energyReceipt1.setHouse(this);
        }
    }

    public void setGasReceipts(List<GasReceipt> gasReceipts) {
        this.gasReceipts = gasReceipts;
        for (GasReceipt gasReceipt : gasReceipts) {
            gasReceipt.setHouse(this);
        }
    }
}
