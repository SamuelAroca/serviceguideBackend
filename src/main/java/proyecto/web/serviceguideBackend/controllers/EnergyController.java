package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.services.EnergyService;

import java.net.URI;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt/energy")
public class EnergyController {

    private final EnergyService energyService;

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

    /*@GetMapping("/findAllByDateByUser/{id}")
    public ResponseEntity<Collection<EnergyReceipt>> findAllByDateByUser(@PathVariable User id) {

        Collection<EnergyReceipt> findAllByDateByUser = energyService.findAllByDateByUser();

        return ResponseEntity.ok(findAllByUser);
    }*/
}
