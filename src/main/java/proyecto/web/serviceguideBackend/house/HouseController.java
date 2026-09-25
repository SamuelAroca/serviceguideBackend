package proyecto.web.serviceguideBackend.house;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;
import proyecto.web.serviceguideBackend.house.dto.OnlyHouse;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.ReceiptService;
import proyecto.web.serviceguideBackend.user.User;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/house")
public class HouseController {

    private final HouseService houseService;
    private final ReceiptService receiptService;
    private final HouseRepository houseRepository;

    @PostMapping("/add/{idUser}")
    @Transactional
    public ResponseEntity<HouseDto> newHouse(@RequestBody @Valid HouseDto houseDto, @PathVariable Long idUser,
                                              @AuthenticationPrincipal User currentUser){
        requireSelf(idUser, currentUser);
        HouseDto createdHouse = houseService.newHouse(houseDto, idUser);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdHouse.getId()).toUri();
        return ResponseEntity.created(location).body(createdHouse);
    }

    @GetMapping("/findAllByUserOrderById/{idUser}")
    public ResponseEntity<Collection<House>> findAllByUserOrderById(@PathVariable Long idUser,
                                                                     @AuthenticationPrincipal User currentUser){
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(houseService.findAllByUserOrderById(idUser));
    }

    @GetMapping("/getHouseName/{idUser}")
    public ResponseEntity<Collection<String>> getHouseName(@PathVariable Long idUser,
                                                            @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(houseService.getHouseName(idUser));
    }

    @PutMapping("/update/{idHouse}")
    @Transactional
    public Optional<Message> updateHouse(@RequestBody HouseDto houseDto, @PathVariable Long idHouse,
                                          @AuthenticationPrincipal User currentUser){
        requireHouseOwner(idHouse, currentUser);
        return houseService.updateHouse(houseDto, idHouse);
    }

    @DeleteMapping("/delete/{idHouse}")
    @Transactional
    public Message deleteHouse(@PathVariable Long idHouse, @AuthenticationPrincipal User currentUser){
        requireHouseOwner(idHouse, currentUser);
        return houseService.deleteHouse(idHouse);
    }

    // El JWT prueba quien llama, no que el idUser/idHouse del path sea suyo.
    // Sin estos chequeos cualquier usuario autenticado podia leer, modificar
    // o borrar casas de otros con solo cambiar el id en la URL.
    private void requireSelf(Long idUser, User currentUser) {
        if (currentUser == null || !currentUser.getId().equals(idUser)) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    private void requireHouseOwner(Long idHouse, User currentUser) {
        Long ownerId = houseRepository.findUserByHouseId(idHouse);
        if (currentUser == null || ownerId == null || !ownerId.equals(currentUser.getId())) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/onlyHouse")
    public ResponseEntity<Collection<OnlyHouse>> onlyHouse(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(houseService.onlyHouse(token));
    }

    // Sube la casa Y el/los recibo(s) que trae el PDF en un solo paso.
    // Antes solo creaba la casa (houseService.readPDF), obligando a subir
    // el mismo PDF una segunda vez desde "Agregar recibo" para que el
    // recibo quedara registrado. receiptService.readPDF ya hace ambas
    // cosas (crea la casa si no existe y luego los recibos).
    @PostMapping("/read")
    public ResponseEntity<Message> readReceipt(@RequestParam("archivoPdf") MultipartFile archivoPdf, HttpServletRequest request) {
        return ResponseEntity.ok(receiptService.readPDF(archivoPdf, request));
    }

    @GetMapping("/findIdByName/{name}")
    public ResponseEntity<Long> findIdByName(@PathVariable String name){
        return ResponseEntity.ok(houseService.findIdByName(name));
    }
}
