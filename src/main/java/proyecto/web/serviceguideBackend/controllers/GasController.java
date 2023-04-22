package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.dto.GasDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.GasReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.repositories.GasRepository;
import proyecto.web.serviceguideBackend.services.GasService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/receipt/gas")
public class GasController {

    private final GasService gasService;
    private final GasRepository gasRepository;

    @PostMapping("/add")
    @Transactional
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

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<GasReceipt> updateGasReceipt(@RequestBody GasDto gasDto, @PathVariable Long id){

        return Optional.ofNullable(gasRepository.findById(id)
                .map(gasReceipt -> {
                    Optional<GasReceipt> optionalGasReceiptReceipt = gasRepository.findById(id);
                    if (optionalGasReceiptReceipt.isPresent()){
                        gasReceipt.setReceiptName(gasDto.getReceiptName());
                        gasReceipt.setPrice(gasDto.getPrice());
                        gasReceipt.setAmount(gasDto.getAmount());
                        gasReceipt.setDate(gasDto.getDate());

                        return gasRepository.save(gasReceipt);
                    } else {
                        throw new AppException("Gas receipt not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Gas receipt not found", HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteSewerageReceipt(@PathVariable Long id){

        Optional<GasReceipt> optionalGasReceipt = gasRepository.findById(id);
        if (optionalGasReceipt.isEmpty()){
            throw new AppException("Gas receipt not found", HttpStatus.NOT_FOUND);
        }

        gasRepository.delete(optionalGasReceipt.get());

        return ResponseEntity.ok("Delete success");
    }
}
