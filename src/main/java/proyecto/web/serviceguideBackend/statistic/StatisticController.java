package proyecto.web.serviceguideBackend.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.averageStatistic.dto.PercentageStatisticDto;
import proyecto.web.serviceguideBackend.averageStatistic.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;

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

    @GetMapping("/getStatisticByType/{typeReceipt}/{house}/{idUser}")
    public ResponseEntity<StatisticAverageDto> getStatisticByTypeAndHouse(@PathVariable String house, @PathVariable Long idUser, @PathVariable String typeReceipt) {
        return ResponseEntity.ok(statisticService.getStatisticByTypeAndHouse(house, idUser, typeReceipt));
    }

    @GetMapping("/getStatisticByYear/{typeReceipt}/{year}/{idUser}")
    public ResponseEntity<StatisticAverageDto> getStatisticByTypeAndYear(@PathVariable String typeReceipt, @PathVariable int year, @PathVariable Long idUser) {
        return ResponseEntity.ok(statisticService.getStatisticByTypeAndYear(typeReceipt, idUser, year));
    }

    @GetMapping("/getStatisticByQuarter/{typeReceipt}/{quarter}/{year}/{idUser}")
    public ResponseEntity<StatisticAverageDto> getStatisticByQuarter(@PathVariable String typeReceipt, @PathVariable int quarter, @PathVariable int year, @PathVariable Long idUser) {
        return ResponseEntity.ok(statisticService.getStatisticByQuarter(idUser, typeReceipt, quarter, year));
    }

    @GetMapping("/getStatisticBySemester/{typeReceipt}/{semester}/{year}/{idUser}")
    public ResponseEntity<StatisticAverageDto> getStatisticBySemester(@PathVariable String typeReceipt, @PathVariable int semester, @PathVariable int year, @PathVariable Long idUser) {
        return ResponseEntity.ok(statisticService.getStatisticBySemester(idUser, typeReceipt, semester, year));
    }

    @GetMapping("/getStatisticByMonth/{typeReceipt}/{startMonth}/{endMonth}/{receiptYear}/{idUser}")
    public ResponseEntity<StatisticAverageDto> getStatisticByMonth(@PathVariable Long idUser, @PathVariable String typeReceipt, @PathVariable int startMonth, @PathVariable int endMonth, @PathVariable int receiptYear) {
        return ResponseEntity.ok(statisticService.getStatisticByMonth(idUser, typeReceipt, startMonth, endMonth, receiptYear));
    }

    @GetMapping("/getPercentage/{houseName}/{idUser}")
    public ResponseEntity<PercentageStatisticDto> getPercentage(@PathVariable Long idUser, @PathVariable String houseName){
        return ResponseEntity.ok(statisticService.getPercentage(idUser, houseName));
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
}
