package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.entities.House;

@Mapper(componentModel = "spring")
public interface HouseMapper {

    HouseDto houseDto(House house);

    House newHouse(HouseDto houseDto);
}
