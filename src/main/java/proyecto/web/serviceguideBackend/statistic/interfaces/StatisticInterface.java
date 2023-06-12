package proyecto.web.serviceguideBackend.statistic.interfaces;

import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;

public interface StatisticInterface {

    StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic);
    double[] sumStatisticByType(Long idUser, String house);
    SumOfReceiptDto sumOfReceiptDto(Long idHouse);

}
