package proyecto.web.serviceguideBackend.house.interfaces;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;

import java.util.Collection;
import java.util.Optional;

public interface HouseInterface {

    HouseDto newHouse(HouseDto houseDto, Long idUser);
    Collection<House> findAllByUserOrderById(Long idUser);
    Optional<Message> updateHouse(HouseDto houseDto, Long id);
    Optional<House> findByUserAndName(Long idUser, String name);
    Message deleteHouse(Long id);
    Collection<String> getHouseName(Long idUser);
}
