package proyecto.web.serviceguideBackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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
    private Double amount;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "FK_HOUSE")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private House house;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<Statistic> statistics;



}
