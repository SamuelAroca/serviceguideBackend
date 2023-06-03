package proyecto.web.serviceguideBackend.city.interfaces;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.city.City;
import proyecto.web.serviceguideBackend.city.CityDto;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto colombianCitiesDto(City city);

    City colombianCities(CityDto cityDto);
}
