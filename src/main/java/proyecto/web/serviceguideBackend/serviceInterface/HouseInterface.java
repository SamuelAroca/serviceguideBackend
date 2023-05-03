package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HouseInterface {

    HouseDto newHouse(HouseDto houseDto);
    Collection<House> findAllByUserOrderById(String token);
    Optional<House> findByUserAndName(User user, String name);
    Optional<Message> updateHouse(HouseDto houseDto, Long id);
    Message deleteHouse(Long id);

    List<String> prueba(Long id);
}
