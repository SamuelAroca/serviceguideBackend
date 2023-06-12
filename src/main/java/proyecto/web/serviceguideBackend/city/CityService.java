package proyecto.web.serviceguideBackend.city;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.city.interfaces.CityInterface;
import proyecto.web.serviceguideBackend.city.interfaces.CityRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class CityService implements CityInterface {

    private final CityRepository cityRepository;

    @Override
    public Collection<City> listAll() {
        return cityRepository.findAll();
    }
}
