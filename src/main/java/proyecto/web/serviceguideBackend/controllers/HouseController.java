package proyecto.web.serviceguideBackend.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.repositories.ColombianCitiesRepository;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.services.HouseService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/house")
public class HouseController {

    private final HouseService houseService;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<HouseDto> newHouse(@RequestBody @Valid HouseDto houseDto){

        HouseDto createdHouse = houseService.newHouse(houseDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdHouse.getId()).toUri();
        return ResponseEntity.created(location).body(createdHouse);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<House>> findAllByUser(@PathVariable User id){
        return ResponseEntity.ok(houseService.findAllByUser(id));
    }

    @GetMapping("/findNameById/{id}")
    public ResponseEntity<Optional<House>> findNameById(@PathVariable Long id) {
        return ResponseEntity.ok(houseService.findNameById(id));
    }

    @GetMapping("/findIdByName/{name}")
    public ResponseEntity<Optional<House>> findIdByName(@PathVariable String name) {
        return ResponseEntity.ok(houseService.findIdByName(name));
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<Message> updateHouse(@RequestBody HouseDto houseDto, @PathVariable Long id){
        return houseService.updateHouse(houseDto, id);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public Message deleteHouse(@PathVariable Long id){
        return houseService.deleteHouse(id);
    }
}
