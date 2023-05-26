package proyecto.web.serviceguideBackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatisticAverageDto {

    @NotNull
    private String houseName;

    @NotNull
    private Double amount;

    @NotNull
    private Double price;

    @NotNull
    private Double averagePrice;

    @NotNull
    private Double averageAmount;

}
