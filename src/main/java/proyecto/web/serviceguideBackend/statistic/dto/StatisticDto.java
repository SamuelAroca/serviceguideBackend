package proyecto.web.serviceguideBackend.statistic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proyecto.web.serviceguideBackend.statistic.statisticType.StatisticType;

@Getter
@Setter
@NoArgsConstructor
public class StatisticDto {

    @NotNull
    private Long id;

    @NotNull
    private String[] label;

    @NotNull
    private Double[] price;

    @NotNull
    private Double[] amount;

    @NotNull
    private StatisticType statisticsType;

}
