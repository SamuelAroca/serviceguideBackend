package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.NewReceiptDto;
import proyecto.web.serviceguideBackend.repositories.ReceiptsRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.services.ReceiptService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipts")
public class ReceiptsController {

    private final ReceiptService receiptService;
    private final UserRepository userRepository;
    private final ReceiptsRepository receiptsRepository;

    @PostMapping("/add")
    ResponseEntity<NewReceiptDto> addReceipts(@RequestBody @Valid NewReceiptDto receipt) {
        NewReceiptDto createdReceipt = receiptService.addNewReceipt(receipt);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdReceipt.getId()).toUri();
        return ResponseEntity.created(location).body(createdReceipt);
    }
}
