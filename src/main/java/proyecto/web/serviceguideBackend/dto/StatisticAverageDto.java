package proyecto.web.serviceguideBackend.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatisticAverageDto {

    @Nullable
    private String houseName;

    @Nullable
    private String year;

    @NotNull
    private Double amount;

    @NotNull
    private Double price;

    @NotNull
    private Double averagePrice;

    @NotNull
    private Double averageAmount;

}
