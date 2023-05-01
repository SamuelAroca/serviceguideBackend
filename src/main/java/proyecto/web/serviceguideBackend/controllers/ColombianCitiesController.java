package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.ColombianCitiesDto;
import proyecto.web.serviceguideBackend.entities.ColombianCities;
import proyecto.web.serviceguideBackend.services.ColombianCitiesService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
public class ColombianCitiesController {

    private final ColombianCitiesService colombianCitiesService;

    @PostMapping("/add")
    public ResponseEntity<ColombianCitiesDto> newCity(@RequestBody @Valid ColombianCitiesDto colombianCitiesDto) {
        ColombianCitiesDto createdCity = colombianCitiesService.newCity(colombianCitiesDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCity.getId()).toUri();
        return ResponseEntity.created(location).body(createdCity);
    }

    @GetMapping("/findById/{idCity}")
    public ResponseEntity<Optional<ColombianCities>> findById(@PathVariable Long idCity) {
        return ResponseEntity.ok(colombianCitiesService.findById(idCity));
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<ColombianCities>> listAll() {
        return ResponseEntity.ok(colombianCitiesService.listAll());
    }

    @GetMapping("/findIdByCity/{city}")
    public ResponseEntity<Optional<ColombianCities>> findIdByCity(@PathVariable String city) {
        return ResponseEntity.ok(colombianCitiesService.findIdByCity(city));
    }
}
