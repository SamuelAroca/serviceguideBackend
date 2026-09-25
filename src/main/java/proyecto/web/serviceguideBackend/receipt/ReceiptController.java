package proyecto.web.serviceguideBackend.receipt;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.statistic.StatisticService;
import proyecto.web.serviceguideBackend.user.User;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ReceiptRepository receiptRepository;
    private final StatisticService statisticService;

    @PostMapping("/add/{idUser}")
    public ResponseEntity<ReceiptDto> newReceipt(@Valid @RequestBody ReceiptDto receiptDto, @PathVariable Long idUser,
                                                  @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        ReceiptDto createdReceipt = receiptService.newReceipt(receiptDto, idUser);

        Long idReceipt = createdReceipt.getId();
        String typeReceipt = createdReceipt.getTypeService().name();
        statisticService.individualReceipt(typeReceipt, idReceipt, "BAR");

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdReceipt.getId()).toUri();
        return ResponseEntity.created(location).body(createdReceipt);
    }

    @GetMapping("/allReceiptsByUserId/{idUser}")
    public ResponseEntity<List<Receipt>> allReceiptsByUserId(@PathVariable Long idUser,
                                                              @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(receiptService.allReceiptsByUserId(idUser));
    }

    @PutMapping("/update/{idReceipt}")
    public Message updateReceipt(@RequestBody ReceiptDto receiptDto, @PathVariable Long idReceipt,
                                  @AuthenticationPrincipal User currentUser) {
        requireReceiptOwner(idReceipt, currentUser);
        return receiptService.updateReceipt(receiptDto, idReceipt);
    }

    @DeleteMapping("/delete/{idReceipt}")
    public Message deleteReceipt(@PathVariable Long idReceipt, @AuthenticationPrincipal User currentUser) {
        requireReceiptOwner(idReceipt, currentUser);
        return receiptService.deleteReceipt(idReceipt);
    }

    @GetMapping("/findById/{idReceipt}")
    public Optional<Receipt> findById(@PathVariable Long idReceipt, @AuthenticationPrincipal User currentUser) {
        requireReceiptOwner(idReceipt, currentUser);
        return receiptRepository.findById(idReceipt);
    }

    @GetMapping("/getLastReceipt/{idUser}")
    public Optional<Receipt> getLastReceipt(@PathVariable Long idUser, @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return receiptService.getLastReceipt(idUser);
    }

    @PostMapping("/read")
    public ResponseEntity<Message> readReceipt(@RequestParam("archivoPdf")MultipartFile archivoPdf, HttpServletRequest request) {
        return ResponseEntity.ok(receiptService.readPDF(archivoPdf, request));
    }

    @GetMapping("/getReceiptsByHouseAndUser/{idUser}/{houseId}")
    public ResponseEntity<List<Receipt>> getReceiptsByHouseAndUser(@PathVariable Long idUser, @PathVariable Long houseId,
                                                                    @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(receiptRepository.getAllReceiptsByHouseId(idUser, houseId));
    }

    // El JWT prueba quien llama, no que idUser/idReceipt (del path) le
    // pertenezca. Sin estos chequeos cualquier usuario autenticado podia
    // leer, modificar o borrar recibos de otros con solo cambiar el id.
    private void requireSelf(Long idUser, User currentUser) {
        if (currentUser == null || !currentUser.getId().equals(idUser)) {
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
