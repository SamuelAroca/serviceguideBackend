package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.WaterMapper;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WaterService {

    private final UserRepository userRepository;
    private final WaterRepository waterRepository;
    private final WaterMapper waterMapper;

    public WaterDto newWater(WaterDto waterDto) {
        Optional<User> optionalUser = userRepository.findById(waterDto.getUser().getId());

        if (optionalUser.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        WaterReceipt waterReceipt = waterMapper.newWater(waterDto);
        waterReceipt.setUser(optionalUser.get());

        WaterReceipt waterReceiptSaved = waterRepository.save(waterReceipt);

        return waterMapper.waterDto(waterReceiptSaved);
    }

    public Collection<WaterReceipt> listAll() {
        return waterRepository.findAll();
    }

    public Collection<WaterReceipt> findAllByUser(User userId) {
        return waterRepository.findAllByUser(userId);
    }
}
