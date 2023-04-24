package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.SewerageMapper;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.SewerageRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SewerageService {

    private final HouseRepository houseRepository;
    private final SewerageRepository sewerageRepository;
    private final SewerageMapper sewerageMapper;

    public SewerageDto newSewerage(SewerageDto sewerageDto) {
        Optional<House> optionalHouse = houseRepository.findById(sewerageDto.getHouse().getId());

        if (optionalHouse.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        SewerageReceipt sewerageReceipt = sewerageMapper.newSewerage(sewerageDto);
        sewerageReceipt.setHouse(optionalHouse.get());

        SewerageReceipt sewerageReceiptSaved = sewerageRepository.save(sewerageReceipt);

        return sewerageMapper.sewerageDto(sewerageReceiptSaved);
    }

    public Collection<SewerageReceipt> findAllByHouse(House houseId) {
        return sewerageRepository.findAllByHouse(houseId);
    }
}
