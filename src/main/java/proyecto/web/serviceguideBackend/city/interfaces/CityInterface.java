package proyecto.web.serviceguideBackend.city.interfaces;

import proyecto.web.serviceguideBackend.city.CityDto;
import proyecto.web.serviceguideBackend.city.City;

import java.util.Collection;
import java.util.Optional;

public interface CityInterface {

    CityDto newCity(CityDto cityDto);
    Collection<City> listAll();
    Optional<City> findById(Long id);
    Optional<City> findByCity(String city);

}
