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
import java.util.Optional;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/add")
    public ResponseEntity<ReceiptDto> newReceipt(@Valid @RequestBody ReceiptDto receiptDto) {

        ReceiptDto createdReceipt = receiptService.newReceipt(receiptDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdReceipt.getId()).toUri();
        return ResponseEntity.created(location).body(createdReceipt);
    }

    @GetMapping("/findByHouse/{house}")
    public ResponseEntity<Collection<Receipt>> findByHouse(@PathVariable House house) {
        return ResponseEntity.ok(receiptService.findByHouse(house));
    }

    @GetMapping("/findByTypeService/{typeService}")
    public ResponseEntity<Collection<Receipt>> findByTypeService(@PathVariable TypeService typeService) {
        return ResponseEntity.ok(receiptService.findByTypeService(typeService));
    }

    @GetMapping("/findAllById/{id}")
    public ResponseEntity<Collection<Receipt>> findAllById(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.findAllById(id));
    }

    @PutMapping("/update/{id}")
    public Optional<Message> updateReceipt(@RequestBody ReceiptDto receiptDto, @PathVariable Long id) {
        return receiptService.updateReceipt(receiptDto, id);
    }

    @DeleteMapping("/delete/{id}")
    public Message deleteReceipt(@PathVariable Long id) {
        return receiptService.deleteReceipt(id);
    }
}
