package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.EnergyMapper;
import proyecto.web.serviceguideBackend.repositories.EnergyRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EnergyService {

    private final UserRepository userRepository;
    private final EnergyRepository energyRepository;
    private final EnergyMapper energyMapper;

    public EnergyDto newEnergy(EnergyDto energyDto) {
        Optional<User> optionalUser = userRepository.findById(energyDto.getUser().getId());

        if (optionalUser.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        EnergyReceipt energyReceipt = energyMapper.newEnergy(energyDto);
        energyReceipt.setUser(optionalUser.get());

        EnergyReceipt energyReceiptSaved = energyRepository.save(energyReceipt);

        return energyMapper.energyDto(energyReceiptSaved);
    }

    public Collection<EnergyReceipt> listAll() {
        return energyRepository.findAll();
    }

    public Collection<EnergyReceipt> findAllByUser(User userId) {
        return energyRepository.findAllByUser(userId);
    }
}
