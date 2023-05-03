package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.CityDto;
import proyecto.web.serviceguideBackend.entities.City;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.CityMapper;
import proyecto.web.serviceguideBackend.repositories.CityRepository;
import proyecto.web.serviceguideBackend.serviceInterface.CityInterface;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CityService implements CityInterface {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    public CityDto newCity(CityDto cityDto) {
        City cities = cityMapper.colombianCities(cityDto);

        City citySaved = cityRepository.save(cities);

        return cityMapper.colombianCitiesDto(citySaved);
    }

    @Override
    public Collection<City> listAll() {
        return cityRepository.findAll();
    }

    @Override
    public Optional<City> findById(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);

        if (optionalCity.isEmpty()) {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
        return optionalCity;
    }

    @Override
    public List<String> allCities() {
        return cityRepository.allCities();
    }

    @Override
    public Optional<City> findIdByCity(String city) {
        return cityRepository.findIdByCity(city);
    }
}
