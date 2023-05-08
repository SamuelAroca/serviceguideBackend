package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.CityDto;
import proyecto.web.serviceguideBackend.entities.City;

import java.util.Collection;
import java.util.Optional;

public interface CityInterface {

    CityDto newCity(CityDto cityDto);
    Collection<City> listAll();
    Optional<City> findById(Long id);
    Optional<City> findByCity(String city);

}
