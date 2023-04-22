package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.repositories.EnergyRepository;
import proyecto.web.serviceguideBackend.services.EnergyService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt/energy")
public class EnergyController {

    private final EnergyService energyService;
    private final EnergyRepository energyRepository;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<EnergyDto> newEnergy(@RequestBody @Valid EnergyDto energyDto) {

        EnergyDto createdEnergy = energyService.newEnergy(energyDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdEnergy.getId()).toUri();
        return ResponseEntity.created(location).body(createdEnergy);
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<EnergyReceipt>> listAllEnergy() {

        Collection<EnergyReceipt> listGasReceipt = energyService.listAll();

        return ResponseEntity.ok(listGasReceipt);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<EnergyReceipt>> findAllByUser(@PathVariable User id) {

        Collection<EnergyReceipt> findAllByUser = energyService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<EnergyReceipt> updateEnergyReceipt(@RequestBody EnergyDto energyDto, @PathVariable Long id){

        return Optional.ofNullable(energyRepository.findById(id)
                .map(energyReceipt -> {
                    Optional<EnergyReceipt> optionalEnergyReceipt = energyRepository.findById(id);
                    if (optionalEnergyReceipt.isPresent()){
                        energyReceipt.setReceiptName(energyDto.getReceiptName());
                        energyReceipt.setPrice(energyDto.getPrice());
                        energyReceipt.setAmount(energyDto.getAmount());
                        energyReceipt.setDate(energyDto.getDate());

                        return energyRepository.save(energyReceipt);
                    } else {
                        throw new AppException("Energy receipt not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Energy receipt not found", HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteSewerageReceipt(@PathVariable Long id){

        Optional<EnergyReceipt> optionalEnergyReceipt = energyRepository.findById(id);
        if (optionalEnergyReceipt.isEmpty()){
            throw new AppException("Sewerage receipt not found", HttpStatus.NOT_FOUND);
        }

        energyRepository.delete(optionalEnergyReceipt.get());

        return ResponseEntity.ok("Delete success");
    }
}
