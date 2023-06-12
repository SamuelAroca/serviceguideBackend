package proyecto.web.serviceguideBackend.averageStatistic;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PercentageStatisticDto {


    @NotNull
    private Double sumLastMonth;

    @NotNull
    private Double sumCurrentMonth;

    @NotNull
    private Double difference;

    @NotNull
    private Double percentage;

}
