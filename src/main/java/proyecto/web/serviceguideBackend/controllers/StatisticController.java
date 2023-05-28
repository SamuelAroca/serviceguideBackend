package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;
import proyecto.web.serviceguideBackend.services.StatisticService;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/individualReceipt/{typeGraphic}/{typeReceipt}/{idReceipt}")
    public ResponseEntity<StatisticDto> individualReceipt(@PathVariable String typeReceipt, @PathVariable Long idReceipt, @PathVariable String typeGraphic) {
        return ResponseEntity.ok(statisticService.individualReceipt(typeReceipt, idReceipt, typeGraphic));
    }

    @GetMapping("/prueba/{idReceipt}")
    public List<Statistic> prueba(@PathVariable Long idReceipt) {
        return statisticService.getStatisticByReceipt(idReceipt);
    }

    @GetMapping("/getStatisticByType/{typeReceipt}/{house}/{token}")
    public ResponseEntity<StatisticAverageDto> getStatisticByTypeAndHouse(@PathVariable String house, @PathVariable String token, @PathVariable String typeReceipt) {
        return ResponseEntity.ok(statisticService.getStatisticByTypeAndHouse(house, token, typeReceipt));
    }

    @GetMapping("/getStatisticByYear/{typeReceipt}/{year}/{token}")
    public ResponseEntity<StatisticAverageDto> getStatisticByTypeAndYear(@PathVariable String typeReceipt, @PathVariable int year, @PathVariable String token) {
        return ResponseEntity.ok(statisticService.getStatisticByTypeAndYear(typeReceipt, token, year));
    }

    @GetMapping("/getReceiptsByQuarter/{typeReceipt}/{quarter}/{year}/{token}")
    public ResponseEntity<StatisticAverageDto> getReceiptsByQuarter(@PathVariable String typeReceipt, @PathVariable int quarter, @PathVariable int year, @PathVariable String token) {
        return ResponseEntity.ok(statisticService.getReceiptsByQuarter(token, typeReceipt, quarter, year));
    }

}
