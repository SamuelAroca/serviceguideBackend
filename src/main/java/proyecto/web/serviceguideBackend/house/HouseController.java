package proyecto.web.serviceguideBackend.house;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/house")
public class HouseController {

    private final HouseService houseService;

    @PostMapping("/add/{token}")
    @Transactional
    public ResponseEntity<HouseDto> newHouse(@RequestBody @Valid HouseDto houseDto, @PathVariable String token){

        HouseDto createdHouse = houseService.newHouse(houseDto, token);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdHouse.getId()).toUri();
        return ResponseEntity.created(location).body(createdHouse);
    }

    @GetMapping("/findAllByUserOrderById/{token}")
    public ResponseEntity<Collection<House>> findAllByUserOrderById(@PathVariable String token){
        return ResponseEntity.ok(houseService.findAllByUserOrderById(token));
    }

    @GetMapping("/findByUserAndName/{token}/{name}")
    public ResponseEntity<Optional<House>> findByUserAndName(@PathVariable String token, @PathVariable String name){
        return ResponseEntity.ok(houseService.findByUserAndName(token, name));
    }

    @GetMapping("/getHouseName/{token}")
    public ResponseEntity<Collection<String>> getHouseName(@PathVariable String token) {
        return ResponseEntity.ok(houseService.getHouseName(token));
    }

    @PutMapping("/update/{idHouse}")
    @Transactional
    public Optional<Message> updateHouse(@RequestBody HouseDto houseDto, @PathVariable Long idHouse){
        return houseService.updateHouse(houseDto, idHouse);
    }

    @DeleteMapping("/delete/{idHouse}")
    @Transactional
    public Message deleteHouse(@PathVariable Long idHouse){
        return houseService.deleteHouse(idHouse);
    }
}
