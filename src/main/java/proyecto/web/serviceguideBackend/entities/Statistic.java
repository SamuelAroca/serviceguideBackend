package proyecto.web.serviceguideBackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "statistics")
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "label", columnDefinition = "TEXT[]")
    private String[] label;

    @Column(name = "data", columnDefinition = "DOUBLE PRECISION[]")
    private Double[] data;

    @OneToOne
    @JoinColumn(name = "FK_TYPE", nullable = false)
    @NotNull
    private StatisticType statisticsType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_RECEIPTS")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Receipt receipt;
}
