package proyecto.web.serviceguideBackend.house;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.city.interfaces.CityRepository;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;
import proyecto.web.serviceguideBackend.house.dto.OnlyHouse;
import proyecto.web.serviceguideBackend.house.interfaces.HouseInterface;
import proyecto.web.serviceguideBackend.house.interfaces.HouseMapper;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HouseService implements HouseInterface {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;
    private final CityRepository cityRepository;
    private final JwtService jwtService;

    @Override
    public HouseDto newHouse(HouseDto houseDto, Long idUser){
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()){
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), houseDto.getName());
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
    public Collection<House> findAllByUserOrderById(Long idUser){
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        return houseRepository.findAllByUserOrderById(optionalUser.get());
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
                        if (optionalHouse.get().getName().equals(houseDto.getName())) {
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
                        }
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
    public Optional<House> findByUserAndName(Long idUser, String name) {
        Optional<User> optionalUser = userRepository.findById(idUser);
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
    public Message deleteHouse(Long id) {
        Optional<House> optionalHouse = houseRepository.findById(id);
        if (optionalHouse.isEmpty()){
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        houseRepository.delete(optionalHouse.get());
        return new Message("Delete success", HttpStatus.OK);
    }

    @Override
    public Collection<String> getHouseName(Long idUser) {
        return houseRepository.getHouseName(idUser);
    }

    @Override
    public Collection<OnlyHouse> onlyHouse(String token) {

        Long idUser = jwtService.whoIsMyId(token);

        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Collection<House> houseList = houseRepository.findAllByUserOrderById(optionalUser.get());

        Collection<OnlyHouse> onlyHouses = new ArrayList<>();
        for (House house : houseList) {
            OnlyHouse onlyHouse = new OnlyHouse(
                    house.getId(),
                    house.getName(),
                    house.getStratum(),
                    house.getNeighborhood(),
                    house.getAddress(),
                    house.getContract(),
                    house.getCities());

            onlyHouses.add(onlyHouse);
        }
        return onlyHouses;
    }
}
