package proyecto.web.serviceguideBackend.statistic.interfaces;

import proyecto.web.serviceguideBackend.averageStatistic.StatisticAverageDto;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.StatisticDto;

import java.util.List;

public interface StatisticInterface {

    StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic);
    List<Statistic> getStatisticByReceipt(Long idReceipt);
    StatisticAverageDto getStatisticByTypeAndHouse(String typeReceipt, String token, String house);
    StatisticAverageDto getStatisticByTypeAndYear(String typeReceipt, String token, int year);
    StatisticAverageDto getStatisticByQuarter(String token, String type, int quarter, int year);
    StatisticAverageDto getStatisticBySemester(String token, String typeReceipt, int semester, int receiptYear);
    StatisticAverageDto getStatisticByMonth(String token, String typeReceipt, int startMonth, int endMonth, int receiptYear);
}
