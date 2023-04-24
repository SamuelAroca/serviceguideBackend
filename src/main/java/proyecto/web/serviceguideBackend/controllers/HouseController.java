package proyecto.web.serviceguideBackend.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
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
    private final HouseRepository houseRepository;

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

        Collection<House> findAllByUser = houseService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<House> updateHouse(@RequestBody HouseDto houseDto, @PathVariable Long id){

        return Optional.ofNullable(houseRepository.findById(id)
                .map(house -> {
                    Optional<House> optionalHouse = houseRepository.findById(id);
                    if (optionalHouse.isPresent()){
                        house.setName(houseDto.getName());
                        house.setStratum(houseDto.getStratum());
                        house.setCity(houseDto.getCity());
                        house.setNeighborhood(houseDto.getNeighborhood());
                        house.setAddress(houseDto.getAddress());
                        house.setContract(houseDto.getContract());

                        return houseRepository.save(house);
                    } else {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("House not found", HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteHouse(@PathVariable Long id){

        Optional<House> optionalHouse = houseRepository.findById(id);
        if (optionalHouse.isEmpty()){
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }

        houseRepository.delete(optionalHouse.get());

        return ResponseEntity.ok("Delete success");
    }
}