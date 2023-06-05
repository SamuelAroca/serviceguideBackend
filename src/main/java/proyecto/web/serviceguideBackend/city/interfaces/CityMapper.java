package proyecto.web.serviceguideBackend.city.interfaces;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.city.dto.CityDto;

@Mapper(componentModel = "spring")
@Component
public interface CityMapper {

    CityDto colombianCitiesDto(City city);

    City colombianCities(CityDto cityDto);

}
