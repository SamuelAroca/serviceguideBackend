package proyecto.web.serviceguideBackend.receipt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.statistic.StatisticService;

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
    public ResponseEntity<ReceiptDto> newReceipt(@Valid @RequestBody ReceiptDto receiptDto, @PathVariable Long idUser) {

        ReceiptDto createdReceipt = receiptService.newReceipt(receiptDto, idUser);

        Long idReceipt = createdReceipt.getId();
        String typeReceipt = createdReceipt.getTypeService().name();
        statisticService.individualReceipt(typeReceipt, idReceipt, "BAR");

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdReceipt.getId()).toUri();
        return ResponseEntity.created(location).body(createdReceipt);
    }

    @GetMapping("/allReceiptsByUserId/{idUser}")
    public ResponseEntity<List<Receipt>> allReceiptsByUserId(@PathVariable Long idUser) {
        return ResponseEntity.ok(receiptService.allReceiptsByUserId(idUser));
    }

    @PutMapping("/update/{idReceipt}")
    public Message updateReceipt(@RequestBody ReceiptDto receiptDto, @PathVariable Long idReceipt) {
        return receiptService.updateReceipt(receiptDto, idReceipt);
    }

    @DeleteMapping("/delete/{idReceipt}")
    public Message deleteReceipt(@PathVariable Long idReceipt) {
        return receiptService.deleteReceipt(idReceipt);
    }

    @GetMapping("/findById/{idReceipt}")
    public Optional<Receipt> findById(@PathVariable Long idReceipt) {
        return receiptRepository.findById(idReceipt);
    }

    @GetMapping("/getLastReceipt/{idUser}")
    public Optional<Receipt> getLastReceipt(@PathVariable Long idUser) {
        return receiptService.getLastReceipt(idUser);
    }

    @PostMapping("/read")
    public ResponseEntity<Message> readReceipt(@RequestParam("archivoPdf")MultipartFile archivoPdf) {
        return ResponseEntity.ok(receiptService.readPDF(archivoPdf));
    }
}
