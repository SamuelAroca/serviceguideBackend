package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;

import java.util.List;

public interface StatisticInterface {

    StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic);
    List<Statistic> getStatisticByReceipt(Long idReceipt);
    StatisticAverageDto getStatisticByTypeAndHouse(String typeReceipt, String token, String house);

}
