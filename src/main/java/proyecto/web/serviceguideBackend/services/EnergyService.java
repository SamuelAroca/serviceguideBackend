package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.EnergyMapper;
import proyecto.web.serviceguideBackend.repositories.EnergyRepository;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EnergyService {

    private final HouseRepository houseRepository;
    private final EnergyRepository energyRepository;
    private final EnergyMapper energyMapper;

    public EnergyDto newEnergy(EnergyDto energyDto) {

        Optional<House> optionalHouse = houseRepository.findById(energyDto.getHouse().getId());

        if (optionalHouse.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        EnergyReceipt energyReceipt = energyMapper.newEnergy(energyDto);
        energyReceipt.setHouse(optionalHouse.get());

        EnergyReceipt energyReceiptSaved = energyRepository.save(energyReceipt);

        return energyMapper.energyDto(energyReceiptSaved);
    }

    public Collection<EnergyReceipt> findAllByHouse(House houseId) {
        return energyRepository.findAllByHouse(houseId);
    }
}
