package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.entities.ColombianCities;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.HouseMapper;
import proyecto.web.serviceguideBackend.repositories.ColombianCitiesRepository;
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
    private final ColombianCitiesRepository colombianCitiesRepository;

    public HouseDto newHouse(HouseDto houseDto){

        Optional<User> optionalUser = userRepository.findById(houseDto.getUser().getId());
        Optional<ColombianCities> optionalColombianCities = colombianCitiesRepository.findByCity(houseDto.getCities().getCity());
        if (optionalColombianCities.isPresent()) {
            Optional<ColombianCities> citiesOptional = colombianCitiesRepository.findById(optionalColombianCities.get().getId());

            if (citiesOptional.isEmpty()) {
                throw new AppException("City not found", HttpStatus.NOT_FOUND);
            }
            if (optionalUser.isEmpty()){
                throw new AppException("User not found", HttpStatus.NOT_FOUND);
            }
            House house = houseMapper.newHouse(houseDto);
            house.setUser(optionalUser.get());
            house.setCities(citiesOptional.get());

            House houseSaved = houseRepository.save(house);
            return houseMapper.houseDto(houseSaved);
        } else {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
    }

    public Collection<House> findAllByUser(User userId){
        return houseRepository.findAllByUser(userId);
    }

    public Optional<House> findNameById(Long id) {
        return houseRepository.findNameById(id);
    }

    public Optional<House> findIdByName(String name) {
        return houseRepository.findIdByName(name);
    }

    public Optional<Message> updateHouse(HouseDto houseDto, Long id) {
        return Optional.of(houseRepository.findById(id)
                .map(house -> {
                    Optional<House> optionalHouse = houseRepository.findById(id);
                    if (optionalHouse.isPresent()){
                        house.setName(houseDto.getName());
                        house.setStratum(houseDto.getStratum());
                        house.setCities(houseDto.getCities());
                        house.setNeighborhood(houseDto.getNeighborhood());
                        house.setAddress(houseDto.getAddress());
                        house.setContract(houseDto.getContract());
                        houseRepository.save(house);
                        return new Message("House updated successfully", HttpStatus.OK);
                    } else {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("House not found", HttpStatus.NOT_FOUND)));
    }

    public Message deleteHouse(Long id) {
        Optional<House> optionalHouse = houseRepository.findById(id);
        if (optionalHouse.isEmpty()){
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        houseRepository.delete(optionalHouse.get());
        return new Message("Delete success", HttpStatus.OK);
    }
}
