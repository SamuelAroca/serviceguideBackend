package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.Water;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;
import proyecto.web.serviceguideBackend.services.WaterService;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt/water")
public class WaterController {

    private final WaterService waterService;

    @PostMapping("/add")
    public ResponseEntity<WaterDto> newWater(@RequestBody @Valid WaterDto waterDto) {
        WaterDto createdWater = waterService.newWater(waterDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdWater.getId()).toUri();
        return ResponseEntity.created(location).body(createdWater);
    }

    @GetMapping("listAll")
    public ResponseEntity<Collection<Water>> listAllWater() {

        Collection<Water> listWater = waterService.listAll();

        return ResponseEntity.ok(listWater);
    }
}
