package proyecto.web.serviceguideBackend.statistic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SumOfReceiptDto {

    private Float sumMonth;
    private Float lastSumMonth;
    private Float difference;
    private Float percentage;

}
