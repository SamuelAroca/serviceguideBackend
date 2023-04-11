package proyecto.web.serviceguideBackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"login"})})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @Size(max = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(max = 100)
    private String lastName;

    @Column(nullable = false)
    @Size(max = 100)
    private String login;

    @Column(nullable = false)
    @Size(max = 100)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WaterReceipt> waterReceipts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SewerageReceipt> sewerageReceipt = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EnergyReceipt> energyReceipts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GasReceipt> gasReceipts = new ArrayList<>();

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWaterReceipts(List<WaterReceipt> waterReceipts) {
        this.waterReceipts = waterReceipts;
        for (WaterReceipt waterReceipt : waterReceipts) {
            waterReceipt.setUser(this);
        }
    }

    public void setSewerageReceipt(List<SewerageReceipt> sewerageReceipt) {
        this.sewerageReceipt = sewerageReceipt;
        for (SewerageReceipt sewerageReceipt1 : sewerageReceipt) {
            sewerageReceipt1.setUser(this);
        }
    }

    public void setEnergyReceipts(List<EnergyReceipt> energyReceipts) {
        this.energyReceipts = energyReceipts;
        for (EnergyReceipt energyReceipt1 : energyReceipts) {
            energyReceipt1.setUser(this);
        }
    }

    public void setGasReceipts(List<GasReceipt> gasReceipts) {
        this.gasReceipts = gasReceipts;
        for (GasReceipt gasReceipt : gasReceipts) {
            gasReceipt.setUser(this);
        }
    }
}
