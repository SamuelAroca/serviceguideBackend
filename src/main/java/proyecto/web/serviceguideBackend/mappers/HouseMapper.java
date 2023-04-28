package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import proyecto.web.serviceguideBackend.dto.HouseDto;
import proyecto.web.serviceguideBackend.entities.House;

@Mapper(componentModel = "spring")
public interface HouseMapper {

    HouseDto houseDto(House house);

    @Mapping(target = "receipts", ignore = true)
    House newHouse(HouseDto houseDto);
}
