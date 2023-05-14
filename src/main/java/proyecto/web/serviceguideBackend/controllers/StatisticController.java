package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;
import proyecto.web.serviceguideBackend.repositories.StatisticRepository;
import proyecto.web.serviceguideBackend.services.StatisticService;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/prueba/{type}/{idReceipt}")
    public ResponseEntity<StatisticDto> prueba(@PathVariable String type, @PathVariable Long idReceipt) {
        return ResponseEntity.ok(statisticService.individualReceipt(type, idReceipt));
    }

    @GetMapping("/getStatisticByReceipt/{idReceipt}")
    public List<Statistic> getStatisticByReceipt(@PathVariable Long idReceipt) {
        return statisticService.getStatisticByReceipt(idReceipt);
    }
}
