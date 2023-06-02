package proyecto.web.serviceguideBackend.house;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.house.interfaces.HouseInterface;
import proyecto.web.serviceguideBackend.house.interfaces.HouseMapper;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.city.interfaces.CityRepository;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.util.*;

@RequiredArgsConstructor
@Service
public class HouseService implements HouseInterface {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;
    private final CityRepository cityRepository;
    private final UserAuthenticationProvider authenticationProvider;

    @Override
    public HouseDto newHouse(HouseDto houseDto, String token){
        Long idUser = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(houseDto.getUser(), houseDto.getName());
        if (optionalHouse.isPresent()) {
            throw new AppException("House name already registered", HttpStatus.BAD_REQUEST);
        }
        Optional<City> optionalColombianCities = cityRepository.findByCity(houseDto.getCities().getCity());
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

    @Override
    public Collection<House> findAllByUserOrderById(String token){
        Long user = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(user);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        return houseRepository.findAllByUserOrderById(optionalUser.get());
    }

    @Override
    public Optional<House> findByUserAndName(String token, String name) {
        Long id = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), name);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        return optionalHouse;
    }

    @Override
    public Optional<House> findById(Long id) {
        return houseRepository.findById(id);
    }

    @Override
    public Collection<String> getHouseName(String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        return houseRepository.getHouseName(id);
    }

    @Override
    public Optional<Message> updateHouse(HouseDto houseDto, Long id) {
        return Optional.of(houseRepository.findById(id)
                .map(house -> {
                    Long idUser = houseRepository.findUserByHouseId(id);
                    Optional<User> optionalUser = userRepository.findById(idUser);
                    if (optionalUser.isEmpty()) {
                        throw new AppException("User not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), houseDto.getName());
                    if (optionalHouse.isPresent()) {
                        throw new AppException("House name already registered", HttpStatus.BAD_REQUEST);
                    }
                    Optional<City> optionalColombianCities = cityRepository.findByCity(houseDto.getCities().getCity());
                    if (optionalColombianCities.isEmpty()) {
                        throw new AppException("City not found", HttpStatus.NOT_FOUND);
                    }
                    house.setName(houseDto.getName());
                    house.setStratum(houseDto.getStratum());
                    house.setNeighborhood(houseDto.getNeighborhood());
                    house.setAddress(houseDto.getAddress());
                    house.setContract(houseDto.getContract());
                    house.setCities(optionalColombianCities.get());
                    houseRepository.save(house);
                    return new Message("House Updated successfully", HttpStatus.OK);
                }).orElseThrow(() -> new AppException("House not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public Message deleteHouse(Long id) {
        Optional<House> optionalHouse = houseRepository.findById(id);
        if (optionalHouse.isEmpty()){
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        houseRepository.delete(optionalHouse.get());
        return new Message("Delete success", HttpStatus.OK);
    }

    @Override
    public Long findIdByName(String name) {
        return houseRepository.findIdByName(name);
    }
}
