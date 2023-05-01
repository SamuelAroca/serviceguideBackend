package proyecto.web.serviceguideBackend.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
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

    @GetMapping("/findAllByUserOrderById/{user}")
    public ResponseEntity<Collection<House>> findAllByUserOrderById(@PathVariable User user){
        return ResponseEntity.ok(houseService.findAllByUserOrderById(user));
    }

    @GetMapping("/findByUserAndName/{user}/{name}")
    public ResponseEntity<Optional<House>> findByUserAndName(@PathVariable User user, @PathVariable String name){
        return ResponseEntity.ok(houseService.findByUserAndName(user, name));
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
