package proyecto.web.serviceguideBackend.statistic.interfaces;

import proyecto.web.serviceguideBackend.averageStatistic.dto.PercentageStatisticDto;
import proyecto.web.serviceguideBackend.averageStatistic.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;

import java.util.List;

public interface StatisticInterface {

    StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic);
    List<Statistic> getStatisticByReceipt(Long idReceipt);
    StatisticAverageDto getStatisticByTypeAndHouse(String typeReceipt, Long idUser, String house);
    StatisticAverageDto getStatisticByTypeAndYear(String typeReceipt, Long idUser, int year);
    StatisticAverageDto getStatisticByQuarter(Long idUser, String type, int quarter, int year);
    StatisticAverageDto getStatisticBySemester(Long idUser, String typeReceipt, int semester, int receiptYear);
    StatisticAverageDto getStatisticByMonth(Long idUser, String typeReceipt, int startMonth, int endMonth, int receiptYear);
    double[] sumStatisticByType(Long idUser, String house);
    PercentageStatisticDto getPercentage(Long idUser, String houseName);
    SumOfReceiptDto sumOfReceiptDto(Long idHouse);

}
