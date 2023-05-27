package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/getStatisticByType/{typeGraphic}/{house}/{token}")
    public ResponseEntity<StatisticAverageDto> getStatisticByTypeAndHouse(@PathVariable String house, @PathVariable String token, @PathVariable String typeGraphic) {
        return ResponseEntity.ok(statisticService.getStatisticByTypeAndHouse(house, token, typeGraphic));
    }
}
