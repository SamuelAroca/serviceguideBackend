package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;
import proyecto.web.serviceguideBackend.services.WaterService;

import java.net.URI;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt/water")
public class WaterController {

    private final WaterService waterService;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<WaterDto> newWater(@RequestBody @Valid WaterDto waterDto) {
        WaterDto createdWater = waterService.newWater(waterDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdWater.getId()).toUri();
        return ResponseEntity.created(location).body(createdWater);
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<WaterReceipt>> listAllWater() {

        Collection<WaterReceipt> listWaterReceipt = waterService.listAll();

        return ResponseEntity.ok(listWaterReceipt);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<WaterReceipt>> findAllByUser(@PathVariable User id) {

        Collection<WaterReceipt> findAllByUser = waterService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }
}
