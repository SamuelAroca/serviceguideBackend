package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;
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

    @PutMapping("/update/{idReceipt}/{token}")
    public Optional<Message> updateReceipt(@RequestBody ReceiptDto receiptDto, @PathVariable Long idReceipt, @PathVariable String token) {
        return receiptService.updateReceipt(receiptDto, idReceipt, token);
    }

    @DeleteMapping("/delete/{idReceipt}")
    public Message deleteReceipt(@PathVariable Long idReceipt) {
        return receiptService.deleteReceipt(idReceipt);
    }
}
