package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.ColombianCitiesDto;
import proyecto.web.serviceguideBackend.entities.ColombianCities;

@Mapper(componentModel = "spring")
public interface ColombianCitiesMapper {

    ColombianCitiesDto colombianCitiesDto(ColombianCities colombianCities);

    ColombianCities colombianCities(ColombianCitiesDto colombianCitiesDto);
}
