package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.CityDto;
import proyecto.web.serviceguideBackend.entities.City;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDto colombianCitiesDto(City city);

    City colombianCities(CityDto cityDto);
}
