package proyecto.web.serviceguideBackend.city;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.city.interfaces.CityInterface;
import proyecto.web.serviceguideBackend.city.interfaces.CityRepository;

import java.util.Collection;
import java.util.Optional;

// Ciudades: catálogo de solo lectura (no existe endpoint de alta/edición/borrado),
// por eso se cachea sin necesidad de invalidación.
@RequiredArgsConstructor
@Service
public class CityService implements CityInterface {

    private final CityRepository cityRepository;

    @Override
    @Cacheable("cities")
    public Collection<City> listAll() {
        return cityRepository.findAllByOrderById();
    }

    @Override
    @Cacheable(value = "cityByName", key = "#city")
    public Optional<City> findByCity(String city) {
        return cityRepository.findByCity(city);
    }
}
