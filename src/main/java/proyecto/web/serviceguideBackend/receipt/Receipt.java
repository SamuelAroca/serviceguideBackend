package proyecto.web.serviceguideBackend.receipt;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.house.House;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100)
    private String receiptName;

    @NotNull
    @Column(length = 100)
    private Double price;

    @NotNull
    @Column(length = 100)
    private Float amount;

    @Nullable
    @Column(length = 100)
    private String houseName;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private Date date;

    @OneToOne
    @JoinColumn(name = "FK_TYPE_SERVICE", nullable = false)
    @NotNull
    private TypeService typeService;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_HOUSE")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private House house;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinTable(
            name = "receipt_statistic", joinColumns = @JoinColumn(name = "FK_RECEIPT", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "FK_STATISTIC", referencedColumnName = "id")
    )
    private List<Statistic> statistics;
}
