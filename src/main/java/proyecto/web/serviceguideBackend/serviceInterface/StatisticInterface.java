package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Receipt;

import java.util.Optional;

public interface StatisticInterface {

    StatisticDto individualReceipt(Long idReceipt, String type);

}
