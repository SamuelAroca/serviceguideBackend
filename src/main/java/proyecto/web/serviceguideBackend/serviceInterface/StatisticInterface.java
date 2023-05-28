package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;

import java.util.Date;
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
