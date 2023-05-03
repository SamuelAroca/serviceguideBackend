package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.CityDto;
import proyecto.web.serviceguideBackend.entities.City;
import proyecto.web.serviceguideBackend.services.CityService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    @PostMapping("/add")
    public ResponseEntity<CityDto> newCity(@RequestBody @Valid CityDto cityDto) {
        CityDto createdCity = cityService.newCity(cityDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdCity.getId()).toUri();
        return ResponseEntity.created(location).body(createdCity);
    }

    @GetMapping("/findById/{idCity}")
    public ResponseEntity<Optional<City>> findById(@PathVariable Long idCity) {
        return ResponseEntity.ok(cityService.findById(idCity));
    }

    @GetMapping("/listAll")
    public ResponseEntity<Collection<City>> listAll() {
        return ResponseEntity.ok(cityService.listAll());
    }

    @GetMapping("/allCities")
    public ResponseEntity<Collection<String>> allCities() {
        return ResponseEntity.ok(cityService.allCities());
    }

    @GetMapping("/findByCity/{city}")
    public ResponseEntity<Optional<City>> findIdByCity(@PathVariable String city) {
        return ResponseEntity.ok(cityService.findByCity(city));
    }
}
