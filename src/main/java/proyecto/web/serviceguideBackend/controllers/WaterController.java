package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;
import proyecto.web.serviceguideBackend.services.WaterService;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/receipt/water")
public class WaterController {

    private final WaterService waterService;
    private final WaterRepository waterRepository;

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<WaterDto> newWater(@RequestBody @Valid WaterDto waterDto) {
        WaterDto createdWater = waterService.newWater(waterDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdWater.getId()).toUri();
        return ResponseEntity.created(location).body(createdWater);
    }

    @GetMapping("/findAllByUser/{id}")
    public ResponseEntity<Collection<WaterReceipt>> findAllByUser(@PathVariable User id) {

        Collection<WaterReceipt> findAllByUser = waterService.findAllByUser(id);

        return ResponseEntity.ok(findAllByUser);
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<WaterReceipt> updateWater(@RequestBody WaterDto waterDto, @PathVariable Long id){

        return Optional.ofNullable(waterRepository.findById(id)
                .map(waterReceipt -> {
                    Optional<WaterReceipt> optionalWaterReceipt = waterRepository.findById(id);
                    if (optionalWaterReceipt.isPresent()){
                        waterReceipt.setReceiptName(waterDto.getReceiptName());
                        waterReceipt.setPrice(waterDto.getPrice());
                        waterReceipt.setAmount(waterDto.getAmount());
                        waterReceipt.setDate(waterDto.getDate());

                        return waterRepository.save(waterReceipt);
                    } else {
                        throw new AppException("Water receipt not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Water receipt not found", HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<String> deleteWaterReceipt(@PathVariable Long id){

        Optional<WaterReceipt> optionalWaterReceipt = waterRepository.findById(id);
        if (optionalWaterReceipt.isEmpty()){
            throw new AppException("Water receipt not found", HttpStatus.NOT_FOUND);
        }

        waterRepository.delete(optionalWaterReceipt.get());

        return ResponseEntity.ok("Delete success");
    }
}
