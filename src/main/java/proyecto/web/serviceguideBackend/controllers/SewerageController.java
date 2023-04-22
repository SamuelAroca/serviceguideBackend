package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.repositories.SewerageRepository;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;
import proyecto.web.serviceguideBackend.services.SewerageService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/receipt/sewerage")
public class SewerageController {

    private final SewerageService sewerageService;
    private final SewerageRepository sewerageRepository;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<SewerageDto> newSewerage(@RequestBody @Valid SewerageDto sewerageDto) {
        SewerageDto createdSewerage = sewerageService.newSewerage(sewerageDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdSewerage.getId()).toUri();
        return ResponseEntity.created(location).body(createdSewerage);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<SewerageReceipt>> findAllByUser(@PathVariable User id) {

        Collection<SewerageReceipt> findAllByUser = sewerageService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<SewerageReceipt> updateSewerageReceipt(@RequestBody SewerageDto sewerageDto, @PathVariable Long id){

        return Optional.ofNullable(sewerageRepository.findById(id)
                .map(sewerageReceipt -> {
                    Optional<SewerageReceipt> optionalSewerageReceipt = sewerageRepository.findById(id);
                    if (optionalSewerageReceipt.isPresent()){
                        sewerageReceipt.setReceiptName(sewerageDto.getReceiptName());
                        sewerageReceipt.setPrice(sewerageDto.getPrice());
                        sewerageReceipt.setAmount(sewerageDto.getAmount());
                        sewerageReceipt.setDate(sewerageDto.getDate());

                        return sewerageRepository.save(sewerageReceipt);
                    } else {
                        throw new AppException("Sewerage receipt not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Sewerage receipt not found", HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteSewerageReceipt(@PathVariable Long id){

        Optional<SewerageReceipt> optionalSewerageReceipt = sewerageRepository.findById(id);
        if (optionalSewerageReceipt.isEmpty()){
            throw new AppException("Sewerage receipt not found", HttpStatus.NOT_FOUND);
        }

        sewerageRepository.delete(optionalSewerageReceipt.get());

        return ResponseEntity.ok("Delete success");
    }
}
