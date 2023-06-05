package proyecto.web.serviceguideBackend.averageStatistic;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "average_statistic")
public class AverageStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "houseName")
    private String houseName;

    @Column(name = "amount")
    @NotNull
    private Double amount;

    @Column(name = "price")
    @NotNull
    private Double price;

    @Column(name = "averagePrice")
    @NotNull
    private Double averagePrice;

    @Column(name = "averageAmount")
    @NotNull
    private Double averageAmount;

    @Column(name = "timestamp")
    @NotNull
    private Timestamp timestamp;

}
