package proyecto.web.serviceguideBackend.city;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    @GetMapping("/listAll")
    public ResponseEntity<Collection<City>> listAll() {
        return ResponseEntity.ok(cityService.listAll());
    }
}
