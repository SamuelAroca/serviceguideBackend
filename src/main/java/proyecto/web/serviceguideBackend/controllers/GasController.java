package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.GasDto;
import proyecto.web.serviceguideBackend.entities.GasReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.services.GasService;

import java.net.URI;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/receipt/gas")
public class GasController {

    private final GasService gasService;

    @PostMapping("/add")
    public ResponseEntity<GasDto> newGas(@RequestBody @Valid GasDto gasDto) {

        GasDto createdGas = gasService.newGas(gasDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdGas.getId()).toUri();
        return ResponseEntity.created(location).body(createdGas);
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<GasReceipt>> listAllGas() {

        Collection<GasReceipt> listGasReceipt = gasService.listAll();

        return ResponseEntity.ok(listGasReceipt);
    }

    @GetMapping("findAllByUser/{id}")
    public ResponseEntity<Collection<GasReceipt>> findAllByUser(@PathVariable User id) {

        Collection<GasReceipt> findAllByUser = gasService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }
}
