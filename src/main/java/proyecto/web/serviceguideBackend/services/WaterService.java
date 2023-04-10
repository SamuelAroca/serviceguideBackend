package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.Water;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.WaterMapper;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.repositories.WaterRepository;

import java.util.Collection;
import java.util.List;
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

        Water water = waterMapper.newWater(waterDto);
        water.setUser(optionalUser.get());

        Water waterSaved = waterRepository.save(water);

        return waterMapper.waterDto(waterSaved);
    }

    public Collection<Water> listAll() {
        return waterRepository.findAll();
    }
}
