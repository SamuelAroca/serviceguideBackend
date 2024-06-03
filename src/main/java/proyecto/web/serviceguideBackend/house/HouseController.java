package proyecto.web.serviceguideBackend.house;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;
import proyecto.web.serviceguideBackend.house.dto.OnlyHouse;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/house")
public class HouseController {

    private final HouseService houseService;

    @PostMapping("/add/{idUser}")
    @Transactional
    public ResponseEntity<HouseDto> newHouse(@RequestBody @Valid HouseDto houseDto, @PathVariable Long idUser){

        HouseDto createdHouse = houseService.newHouse(houseDto, idUser);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdHouse.getId()).toUri();
        return ResponseEntity.created(location).body(createdHouse);
    }

    @GetMapping("/findAllByUserOrderById/{idUser}")
    public ResponseEntity<Collection<House>> findAllByUserOrderById(@PathVariable Long idUser){
        return ResponseEntity.ok(houseService.findAllByUserOrderById(idUser));
    }

    @GetMapping("/getHouseName/{idUser}")
    public ResponseEntity<Collection<String>> getHouseName(@PathVariable Long idUser) {
        return ResponseEntity.ok(houseService.getHouseName(idUser));
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

    @GetMapping("/onlyHouse/{token}")
    public ResponseEntity<Collection<OnlyHouse>> onlyHouse(@PathVariable String token){
        return ResponseEntity.ok(houseService.onlyHouse(token));
    }

    @PostMapping("/read")
    public ResponseEntity<Message> readReceipt(@RequestParam("archivoPdf") MultipartFile archivoPdf, HttpServletRequest request) {
        return ResponseEntity.ok(houseService.readPDF(archivoPdf, request));
    }

    @GetMapping("/findIdByName/{name}")
    public ResponseEntity<Long> findIdByName(@PathVariable String name){
        return ResponseEntity.ok(houseService.findIdByName(name));
    }
}
