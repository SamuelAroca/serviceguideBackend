package proyecto.web.serviceguideBackend.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/individualReceipt/{typeGraphic}/{typeReceipt}/{idReceipt}")
    public ResponseEntity<StatisticDto> individualReceipt(@PathVariable String typeReceipt, @PathVariable Long idReceipt, @PathVariable String typeGraphic) {
        return ResponseEntity.ok(statisticService.individualReceipt(typeReceipt, idReceipt, typeGraphic));
    }

    @GetMapping("/sumStatisticByType/{houseName}/{idUser}")
    public ResponseEntity<double[]> sumStatisticByType(@PathVariable Long idUser, @PathVariable String houseName){
        double[] sums = statisticService.sumStatisticByType(idUser, houseName);
        return ResponseEntity.ok(sums);
    }

    @GetMapping("/informationReceipt/{idHouse}")
    public ResponseEntity<SumOfReceiptDto> informationReceipt(@PathVariable Long idHouse) {
        return ResponseEntity.ok(statisticService.sumOfReceiptDto(idHouse));
    }

    @GetMapping("/generateReportPDF/{userId}/{houseId}")
    public ResponseEntity<ByteArrayResource> generateReportPDF(@PathVariable Long userId, @PathVariable Long houseId) {
        return statisticService.generateReportPDF(userId, houseId);
    }
}
