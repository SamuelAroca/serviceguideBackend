package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;

import java.util.List;

public interface StatisticInterface {

    StatisticDto individualReceipt(String type, Long idReceipt);
    List<Statistic> getStatisticByReceipt(Long idReceipt);

}
