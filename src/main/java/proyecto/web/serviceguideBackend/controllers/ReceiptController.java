package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.services.ReceiptService;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ReceiptRepository receiptRepository;

    @PostMapping("/add/{token}")
    public ResponseEntity<ReceiptDto> newReceipt(@Valid @RequestBody ReceiptDto receiptDto, @PathVariable String token) {

        ReceiptDto createdReceipt = receiptService.newReceipt(receiptDto, token);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdReceipt.getId()).toUri();
        return ResponseEntity.created(location).body(createdReceipt);
    }

    @GetMapping("/findByHouse/{StringHouse}/{token}")
    public ResponseEntity<Collection<Receipt>> findByHouse(@PathVariable String StringHouse, @PathVariable String token) {
        return ResponseEntity.ok(receiptService.findByHouse(StringHouse, token));
    }

    @GetMapping("/findByTypeServiceAndHouse/{StringTypeService}/{StringHouse}/{token}")
    public ResponseEntity<Collection<Receipt>> findByTypeServiceAndHouse(@PathVariable String StringTypeService, @PathVariable String StringHouse, @PathVariable String token) {
        return ResponseEntity.ok(receiptService.findByTypeServiceAndHouse(StringTypeService, StringHouse, token));
    }

    @GetMapping("/allReceiptsByUserId/{token}")
    public ResponseEntity<List<Receipt>> allReceiptsByUserId(@PathVariable String token) {
        return ResponseEntity.ok(receiptService.allReceiptsByUserId(token));
    }

    @PutMapping("/update/{idReceipt}")
    public Optional<Message> updateReceipt(@RequestBody ReceiptDto receiptDto, @PathVariable Long idReceipt) {
        return receiptService.updateReceipt(receiptDto, idReceipt);
    }

    @DeleteMapping("/delete/{idReceipt}")
    public Message deleteReceipt(@PathVariable Long idReceipt) {
        return receiptService.deleteReceipt(idReceipt);
    }

    @GetMapping("/findUserByReceiptId/{idReceipt}")
    public Long findUserByReceiptId(@PathVariable Long idReceipt) {
        return receiptRepository.findUserByReceiptId(idReceipt);
    }

    @GetMapping("/findById/{idReceipt}")
    public Optional<Receipt> findById(@PathVariable Long idReceipt) {
        return receiptRepository.findById(idReceipt);
    }

    @GetMapping("/getLastReceipt/{token}")
    public Optional<Receipt> getLastReceipt(@PathVariable String token) {
        return receiptService.getLastReceipt(token);
    }

    @GetMapping("/getAllReceiptByType/{token}/{type}")
    public Collection<Receipt> getAllReceiptByType(@PathVariable String token, @PathVariable String type) {
        return receiptService.getAllReceiptsByType(token, type);
    }

    @GetMapping("/getTwoReceiptById/{idReceipt1}/{idReceipt2}")
    public Collection<Receipt> getTwoReceiptById(@PathVariable Long idReceipt1, @PathVariable Long idReceipt2) {
        return receiptService.getTwoReceiptById(idReceipt1, idReceipt2);
    }
}
