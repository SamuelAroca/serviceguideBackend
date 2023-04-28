package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.HouseMapper;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HouseService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;

    public HouseDto newHouse(HouseDto houseDto){

        Optional<User> optionalUser = userRepository.findById(houseDto.getUser().getId());

        if (optionalUser.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        House house = houseMapper.newHouse(houseDto);
        house.setUser(optionalUser.get());

        House houseSaved = houseRepository.save(house);

        return houseMapper.houseDto(houseSaved);
    }

    public Collection<House> findAllByUser(User userId){
        return houseRepository.findAllByUser(userId);
    }

    public Optional<House> findOneByName(String name) {
        return houseRepository.findOneByName(name);
    }
}
