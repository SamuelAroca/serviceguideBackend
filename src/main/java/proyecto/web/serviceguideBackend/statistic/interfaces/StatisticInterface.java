package proyecto.web.serviceguideBackend.statistic.interfaces;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;

public interface StatisticInterface {

    StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic);
    double[] sumStatisticByType(Long idUser, String house);
    SumOfReceiptDto sumOfReceiptDto(Long idHouse);
    ResponseEntity<ByteArrayResource> generateReportPDF(Long userId, Long houseId);

}
