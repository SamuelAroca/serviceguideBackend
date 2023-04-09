package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/water")
public class WaterController {

    private final WaterRepository waterRepository;

    @PostMapping("/add")
    ResponseEntity<WaterDto> newWater(@RequestBody WaterDto waterDto) {

        //WaterDto newWater = waterRepository.save(waterDto);
    }
}
