package proyecto.web.serviceguideBackend.house;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import proyecto.web.serviceguideBackend.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class HouseService implements HouseInterface {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseMapper houseMapper;
    private final CityRepository cityRepository;
    private final JwtService jwtService;
    private final Utils utils;

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

    @Override
    public House readPDF(MultipartFile file, HttpServletRequest request) {
        Long idUser = utils.getTokenFromRequest(request);
        return extractReceiptInformation(utils.readPdf(file), idUser);
    }

    @Override
    public Long findIdByName(String name) {
        return houseRepository.findIdByName(name);
    }

    public House extractReceiptInformation(String receiptText, Long idUser) {

        House house = new House();

        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        house.setUser(optionalUser.get());

        // Expresión regular corregida
        String patronStratum = "Estrato:\\s*(\\d+)";
        Pattern patternStratum = Pattern.compile(patronStratum);
        Matcher matcherStratum = patternStratum.matcher(receiptText);
        String stratum = "";
        if (matcherStratum.find()) {
            stratum = matcherStratum.group(1);
            int stratumInt = Integer.parseInt(stratum);
            if (stratumInt < 1 || stratumInt > 6) {
                throw new AppException("Stratum out of range", HttpStatus.BAD_REQUEST);
            }
            house.setStratum(Integer.parseInt(stratum));
        } else {
            throw new AppException("Stratum not found", HttpStatus.NOT_FOUND);
        }

        // Ciudad en Antioquia
        String patronCity = "([\\p{L}\\s]+)\\s*-\\s*Antioquia";
        Pattern patternCity = Pattern.compile(patronCity);
        Matcher matcherCity = patternCity.matcher(receiptText);
        String city = "";
        if (matcherCity.find()) {
            city = matcherCity.group(1).trim();
        } else {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }

        Optional<City> optionalCity = cityRepository.findByCity(city);
        if (optionalCity.isEmpty()) {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
        house.setCities(optionalCity.get());

        String patronAddress = "Dirección de cobro:\\s*(.*)";
        Pattern patternAddress = Pattern.compile(patronAddress);
        Matcher matcherAddress = patternAddress.matcher(receiptText);
        String address = "";
        if (matcherAddress.find()) {
            address = matcherAddress.group(1).trim();
            house.setAddress(address);
        } else {
            throw new AppException("Address not found", HttpStatus.NOT_FOUND);
        }

        String patronContract = "Contrato\\s*(\\d+)";
        Pattern patternContract = Pattern.compile(patronContract);
        Matcher matcherContract = patternContract.matcher(receiptText);
        String contract = "";
        if (matcherContract.find()) {
            contract = matcherContract.group(1);
        } else {
            throw new AppException("Contract not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByContractAndUser(contract, idUser);
        if (optionalHouse.isPresent()) {
            throw new AppException("Contract already registered", HttpStatus.BAD_REQUEST);
        }
        house.setContract(contract);

        String name = "Casa " + contract;
        String neighborhood = "Barrio de " + optionalCity.get().getCity();
        house.setName(name);
        house.setNeighborhood(neighborhood);

        return houseRepository.save(house);
    }
}
