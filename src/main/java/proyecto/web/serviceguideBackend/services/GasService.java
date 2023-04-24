package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.GasDto;
import proyecto.web.serviceguideBackend.entities.GasReceipt;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.GasMapper;
import proyecto.web.serviceguideBackend.repositories.GasRepository;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GasService {

    private final HouseRepository houseRepository;
    private final GasRepository gasRepository;
    private final GasMapper gasMapper;

    public GasDto newGas(GasDto gasDto) {

        Optional<House> optionalHouse = houseRepository.findById(gasDto.getHouse().getId());

        if (optionalHouse.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        GasReceipt gasReceipt = gasMapper.newGas(gasDto);
        gasReceipt.setHouse(optionalHouse.get());

        GasReceipt gasReceiptSaved = gasRepository.save(gasReceipt);

        return gasMapper.gasDto(gasReceiptSaved);
    }

    public Collection<GasReceipt> findAllByHouse(House houseId) {
        return gasRepository.findAllByHouse(houseId);
    }
}
