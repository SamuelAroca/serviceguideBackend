package proyecto.web.serviceguideBackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.StatisticType;


@Getter
@Setter
@NoArgsConstructor
public class StatisticDto {

    @NotNull
    private Long id;

    @NotNull
    private String[] label;

    @NotNull
    private Double[] data;

    @NotNull
    private StatisticType statisticsType;

    @NotNull
    private Receipt receipt;

    public StatisticDto(@NotNull String[] label, @NotNull Double[] data, StatisticType statisticsType, Receipt receipt) {
        this.label = label;
        this.data = data;
        this.statisticsType = statisticsType;
        this.receipt = receipt;
    }

}
