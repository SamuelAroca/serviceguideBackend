package proyecto.web.serviceguideBackend.averageStatistic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SumStatisticDto {

    @NotNull
    private Double waterSum;

    @NotNull
    private Double energySum;

    @NotNull
    private Double gasSum;

    @NotNull
    private Double sewerageSum;
}
