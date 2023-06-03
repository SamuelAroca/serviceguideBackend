package proyecto.web.serviceguideBackend.house.interfaces;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.HouseDto;

import java.util.Collection;
import java.util.Optional;

public interface HouseInterface {

    HouseDto newHouse(HouseDto houseDto, String token);
    Collection<House> findAllByUserOrderById(String token);
    Optional<House> findByUserAndName(String token, String name);
    Optional<House> findById(Long id);
    Collection<String> getHouseName(String token);
    Optional<Message> updateHouse(HouseDto houseDto, Long id);
    Message deleteHouse(Long id);
    Long findIdByName(String name);

}
