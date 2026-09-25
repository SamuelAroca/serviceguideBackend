package proyecto.web.serviceguideBackend.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;
import proyecto.web.serviceguideBackend.user.User;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;
    private final ReceiptRepository receiptRepository;
    private final HouseRepository houseRepository;

    @GetMapping("/individualReceipt/{typeGraphic}/{typeReceipt}/{idReceipt}")
    public ResponseEntity<StatisticDto> individualReceipt(@PathVariable String typeReceipt, @PathVariable Long idReceipt, @PathVariable String typeGraphic,
                                                           @AuthenticationPrincipal User currentUser) {
        requireReceiptOwner(idReceipt, currentUser);
        return ResponseEntity.ok(statisticService.individualReceipt(typeReceipt, idReceipt, typeGraphic));
    }

    @GetMapping("/sumStatisticByType/{houseName}/{idUser}")
    public ResponseEntity<double[]> sumStatisticByType(@PathVariable Long idUser, @PathVariable String houseName,
                                                        @AuthenticationPrincipal User currentUser){
        requireSelf(idUser, currentUser);
        double[] sums = statisticService.sumStatisticByType(idUser, houseName);
        return ResponseEntity.ok(sums);
    }

    @GetMapping("/informationReceipt/{idHouse}")
    public ResponseEntity<SumOfReceiptDto> informationReceipt(@PathVariable Long idHouse, @AuthenticationPrincipal User currentUser) {
        requireHouseOwner(idHouse, currentUser);
        return ResponseEntity.ok(statisticService.sumOfReceiptDto(idHouse));
    }

    @GetMapping("/generateReportPDF/{userId}/{houseId}")
    public ResponseEntity<ByteArrayResource> generateReportPDF(@PathVariable Long userId, @PathVariable Long houseId,
                                                                @AuthenticationPrincipal User currentUser) {
        requireSelf(userId, currentUser);
        requireHouseOwner(houseId, currentUser);
        return statisticService.generateReportPDF(userId, houseId);
    }

    // El JWT prueba quien llama, no que idUser/idHouse/idReceipt (del path)
    // le pertenezca. Sin estos chequeos cualquier usuario autenticado podia
    // ver estadisticas o descargar el PDF de otro.
    private void requireSelf(Long idUser, User currentUser) {
        if (currentUser == null || !currentUser.getId().equals(idUser)) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    private void requireHouseOwner(Long idHouse, User currentUser) {
        Long ownerId = houseRepository.findUserByHouseId(idHouse);
        if (currentUser == null || ownerId == null || !ownerId.equals(currentUser.getId())) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    private void requireReceiptOwner(Long idReceipt, User currentUser) {
        Long ownerId = receiptRepository.findUserByReceiptId(idReceipt);
        if (currentUser == null || ownerId == null || !ownerId.equals(currentUser.getId())) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
