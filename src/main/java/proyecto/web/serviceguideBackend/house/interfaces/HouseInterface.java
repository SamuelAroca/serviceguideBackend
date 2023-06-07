package proyecto.web.serviceguideBackend.house.interfaces;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;

import java.util.Collection;
import java.util.Optional;

public interface HouseInterface {

    HouseDto newHouse(HouseDto houseDto, Long idUser);
    Collection<House> findAllByUserOrderById(Long idUser);
    Optional<House> findByUserAndName(Long idUser, String name);
    Optional<House> findById(Long id);
    Collection<String> getHouseName(Long idUser);
    Optional<Message> updateHouse(HouseDto houseDto, Long id);
    Message deleteHouse(Long id);
    Long findIdByName(String name);

}
