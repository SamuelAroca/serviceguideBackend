package proyecto.web.serviceguideBackend.receipt;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Antes estos endpoints devolvian la lista completa sin limite: un
    // usuario real de esta app ya tiene 856 recibos. El default cubre eso
    // con margen; el tope evita que alguien pida un size absurdo.
    private static final int DEFAULT_PAGE_SIZE = 2000;
    private static final int MAX_PAGE_SIZE = 2000;

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
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                                                              @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(receiptService.allReceiptsByUserId(idUser, pageable(page, size)));
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
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size,
                                                                    @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(receiptRepository.getAllReceiptsByHouseId(idUser, houseId, pageable(page, size)));
    }

    // El JWT prueba quien llama, no que idUser/idReceipt (del path) le
    // pertenezca. Sin estos chequeos cualquier usuario autenticado podia
    // leer, modificar o borrar recibos de otros con solo cambiar el id.
    private void requireSelf(Long idUser, User currentUser) {
        if (currentUser == null || !currentUser.getId().equals(idUser)) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    private Pageable pageable(int page, int size) {
        int clampedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(Math.max(page, 0), clampedSize);
    }

    private void requireReceiptOwner(Long idReceipt, User currentUser) {
        Long ownerId = receiptRepository.findUserByReceiptId(idReceipt);
        if (currentUser == null || ownerId == null || !ownerId.equals(currentUser.getId())) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
