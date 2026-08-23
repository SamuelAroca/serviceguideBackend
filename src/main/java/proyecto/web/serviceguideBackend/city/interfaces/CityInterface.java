package proyecto.web.serviceguideBackend.city.interfaces;

import proyecto.web.serviceguideBackend.city.City;

import java.util.Collection;
import java.util.Optional;

public interface CityInterface {

    Collection<City> listAll();

    Optional<City> findByCity(String city);

}
