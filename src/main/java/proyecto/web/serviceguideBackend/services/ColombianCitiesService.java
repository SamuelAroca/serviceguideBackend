package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.ColombianCitiesDto;
import proyecto.web.serviceguideBackend.entities.ColombianCities;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.ColombianCitiesMapper;
import proyecto.web.serviceguideBackend.repositories.ColombianCitiesRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ColombianCitiesService {

    private final ColombianCitiesRepository colombianCitiesRepository;
    private final ColombianCitiesMapper colombianCitiesMapper;

    public ColombianCitiesDto newCity(ColombianCitiesDto colombianCitiesDto) {
        ColombianCities cities = colombianCitiesMapper.colombianCities(colombianCitiesDto);

        ColombianCities citySaved = colombianCitiesRepository.save(cities);

        return colombianCitiesMapper.colombianCitiesDto(citySaved);
    }

    public Collection<ColombianCities> listAll() {
        return colombianCitiesRepository.findAll();
    }

    public Optional<ColombianCities> findById(Long id) {
        Optional<ColombianCities> optionalCity = colombianCitiesRepository.findById(id);

        if (optionalCity.isEmpty()) {
            throw new AppException("City not found", HttpStatus.NOT_FOUND);
        }
        return optionalCity;
    }

    public Optional<ColombianCities> findOneByCity(String city) {
        return colombianCitiesRepository.findOneByCity(city);
    }

    public Optional<ColombianCities> findIdByCity(String city) {
        return colombianCitiesRepository.findIdByCity(city);
    }
}
