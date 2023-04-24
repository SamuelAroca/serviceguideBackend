package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.WaterMapper;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WaterService {

    private final HouseRepository houseRepository;
    private final WaterRepository waterRepository;
    private final WaterMapper waterMapper;

    public WaterDto newWater(WaterDto waterDto) {
        Optional<House> optionalHouse = houseRepository.findById(waterDto.getHouse().getId());

        if (optionalHouse.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        WaterReceipt waterReceipt = waterMapper.newWater(waterDto);
        waterReceipt.setHouse(optionalHouse.get());

        WaterReceipt waterReceiptSaved = waterRepository.save(waterReceipt);

        return waterMapper.waterDto(waterReceiptSaved);
    }

    public Collection<WaterReceipt> findAllByHouse(House houseId) {
        return waterRepository.findAllByHouse(houseId);
    }
}
