package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HouseService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;
    private final ColombianCitiesRepository colombianCitiesRepository;

    public HouseDto newHouse(HouseDto houseDto){
        Optional<House> optionalHouse = houseRepository.findByUserAndName(houseDto.getUser(), houseDto.getName());
        if (optionalHouse.isPresent()) {
            throw new AppException("House name already registered", HttpStatus.BAD_REQUEST);
        }
        Optional<User> optionalUser = userRepository.findById(Objects.requireNonNull(houseDto.getUser()).getId());
        if (optionalUser.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<ColombianCities> optionalColombianCities = colombianCitiesRepository.findByCity(houseDto.getCities().getCity());
        if (optionalColombianCities.isPresent()) {

            House house = houseMapper.newHouse(houseDto);
            house.setUser(optionalUser.get());
            house.setCities(optionalColombianCities.get());

            House houseSaved = houseRepository.save(house);
            return houseMapper.houseDto(houseSaved);
        } else {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
    }

    public Collection<House> findAllByUser(User userId){
        return houseRepository.findAllByUser(userId);
    }

    public Optional<House> findByUserAndName(User user, String name) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(user, name);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        return houseRepository.findByUserAndName(user, name);
    }

    public Optional<Message> updateHouse(HouseDto houseDto, Long id) {
        return Optional.of(houseRepository.findById(id)
                .map(house -> {
                    Optional<House> optionalHouse = houseRepository.findByUserAndName(houseDto.getUser(), houseDto.getName());
                    if (optionalHouse.isPresent()) {
                        throw new AppException("House name already registered", HttpStatus.BAD_REQUEST);
                    }
                    Optional<House> houseOptional = houseRepository.findById(id);
                    if (houseOptional.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<ColombianCities> optionalColombianCities = colombianCitiesRepository.findByCity(houseDto.getCities().getCity());
                    if (optionalColombianCities.isEmpty()) {
                        throw new AppException("City not found", HttpStatus.NOT_FOUND);
                    }

                    house.setName(houseDto.getName());
                    house.setStratum(houseDto.getStratum());
                    house.setNeighborhood(houseDto.getNeighborhood());
                    house.setAddress(house.getAddress());
                    house.setContract(house.getContract());
                    house.setCities(optionalColombianCities.get());
                    houseRepository.save(house);
                    return new Message("House Updated successfully", HttpStatus.OK);
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
