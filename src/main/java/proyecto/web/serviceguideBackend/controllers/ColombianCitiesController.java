package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.ColombianCitiesDto;
import proyecto.web.serviceguideBackend.entities.ColombianCities;
import proyecto.web.serviceguideBackend.exceptions.AppException;
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

    @GetMapping("/findById/{id}")
    public ResponseEntity<Optional<ColombianCities>> findById(@PathVariable Long id) {

        Optional<ColombianCities> optional = colombianCitiesService.findById(id);

        return ResponseEntity.ok(optional);
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<ColombianCities>> listAll() {

        Collection<ColombianCities> listAll = colombianCitiesService.listAll();

        return ResponseEntity.ok(listAll);
    }

    @GetMapping("/findOneByCity/{city}")
    public ResponseEntity<Optional<ColombianCities>> findOneByCity(@PathVariable String city) {
        Optional<ColombianCities> optional = colombianCitiesService.findOneByCity(city);

        if (optional.isEmpty()) {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(optional);
    }
}
