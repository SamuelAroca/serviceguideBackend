package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.services.SewerageService;

import java.net.URI;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/receipt/sewerage")
public class SewerageController {

    private final SewerageService sewerageService;

    @PostMapping("/add")
    public ResponseEntity<SewerageDto> newSewerage(@RequestBody @Valid SewerageDto sewerageDto) {
        SewerageDto createdSewerage = sewerageService.newSewerage(sewerageDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdSewerage.getId()).toUri();
        return ResponseEntity.created(location).body(createdSewerage);
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<SewerageReceipt>> listAllSewerage() {

        Collection<SewerageReceipt> listSewerageReceipt = sewerageService.listAll();

        return ResponseEntity.ok(listSewerageReceipt);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<SewerageReceipt>> findAllByUser(@PathVariable User id) {

        Collection<SewerageReceipt> findAllByUser = sewerageService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }
}
