package proyecto.web.serviceguideBackend.statistic;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.web.serviceguideBackend.statistic.statisticType.StatisticType;
import proyecto.web.serviceguideBackend.receipt.Receipt;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "statistic")
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "label", columnDefinition = "TEXT[]")
    private String[] label;

    @Column(name = "price", columnDefinition = "DOUBLE PRECISION[]")
    private Double[] price;

    @Column(name = "amount", columnDefinition = "DOUBLE PRECISION[]")
    private Double[] amount;

    @OneToOne
    @JoinColumn(name = "FK_TYPE", nullable = false)
    @NotNull
    private StatisticType statisticsType;

    @ManyToMany(mappedBy = "statistics", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Receipt> receipts;
}
